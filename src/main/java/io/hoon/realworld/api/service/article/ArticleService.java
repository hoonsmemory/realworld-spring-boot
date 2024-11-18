package io.hoon.realworld.api.service.article;

import io.hoon.realworld.api.service.article.request.ArticleCreateServiceRequest;
import io.hoon.realworld.api.service.article.request.ArticleGetArticlesServiceRequest;
import io.hoon.realworld.api.service.article.request.ArticleUpdateServiceRequest;
import io.hoon.realworld.api.service.article.response.ArticleServiceResponse;
import io.hoon.realworld.api.service.profile.ProfileService;
import io.hoon.realworld.api.service.user.UserService;
import io.hoon.realworld.domain.article.Article;
import io.hoon.realworld.domain.article.ArticleRepository;
import io.hoon.realworld.domain.article.favorite.Favorite;
import io.hoon.realworld.domain.article.favorite.FavoriteRepository;
import io.hoon.realworld.domain.article.tag.Tag;
import io.hoon.realworld.domain.article.tag.TagRepository;
import io.hoon.realworld.exception.Error;
import io.hoon.realworld.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final FavoriteRepository favoriteRepository;
    private final ProfileService profileService;
    private final UserService userService;

    @Transactional
    public ArticleServiceResponse createArticle(AuthUser user, ArticleCreateServiceRequest request) {
        Article article = request.toArticleEntity();
        article.addAuthor(user.toEntity());
        List<Tag> tagList = request.toTagEntityList();

        for (Tag requestTag : tagList) {
            Optional<Tag> tag = tagRepository.findByName(requestTag.getName());
            Tag savedTag = tag.orElseGet(() -> tagRepository.save(requestTag));
            article.addTag(savedTag);
        }

        article = articleRepository.save(article);
        return ArticleServiceResponse.of(article, false, false, 0);
    }

    @Transactional
    public ArticleServiceResponse updateArticle(AuthUser user, String slug, ArticleUpdateServiceRequest request) {
        Article article = findArticleBySlugAndValidateAuthor(user.getId(), slug);

        request.getTitle()
               .ifPresent(article::updateTitle);
        request.getDescription()
               .ifPresent(article::updateDescription);
        request.getBody()
               .ifPresent(article::updateBody);

        article = articleRepository.save(article);

        return ArticleServiceResponse.of(article, false, false, article.getFavoriteList().size());
    }

    @Transactional
    public void deleteArticle(AuthUser user, String slug) {
        findArticleBySlugAndValidateAuthor(user.getId(), slug);

        int result = articleRepository.deleteBySlug(slug);
        if (result == 0) {
            throw new IllegalStateException(Error.FAILED_TO_DELETE.getMessage());
        }
    }

    private Article findArticleBySlugAndValidateAuthor(Long authorId, String slug) {
        Article article = findBySlug(slug);

        Long getAuthorId = article.getAuthor().getId();
        if (!getAuthorId.equals(authorId)) {
            throw new IllegalArgumentException(Error.INVALID_TOKEN.getMessage());
        }

        return article;
    }

    @Transactional
    public ArticleServiceResponse favoriteArticle(AuthUser user, String slug) {
        Article article = findBySlug(slug);

        favoriteRepository.findByArticleIdAndUserId(article.getId(), user.getId())
                          .ifPresent(favorite -> {
                              throw new IllegalStateException(Error.ALREADY_FAVORITED.getMessage());
                          });
        
        article.favorite(favoriteRepository.save(Favorite.create(article, user.toEntity())));

        AddtionalInfo addtionalInfo = getArticleAdditionalInfo(user, article);

        return ArticleServiceResponse.of(article, addtionalInfo.isFollowing(), true, addtionalInfo.favoritesCount());
    }
    
    @Transactional
    public ArticleServiceResponse unfavoriteArticle(AuthUser user, String slug) {
        Article article = findBySlug(slug);
        
        article.unfavorite(favoriteRepository.findByArticleIdAndUserId(article.getId(), user.getId())
                                             .orElseThrow(() -> new IllegalArgumentException(Error.NOT_FAVORITED.getMessage())));

        AddtionalInfo addtionalInfo = getArticleAdditionalInfo(user, article);

        return ArticleServiceResponse.of(article, addtionalInfo.isFollowing(), false, addtionalInfo.favoritesCount());
    }

    public ArticleServiceResponse getArticle(AuthUser user, String slug) {
        Article article = findBySlug(slug);

        AddtionalInfo addtionalInfo = getArticleAdditionalInfo(user, article);

        return ArticleServiceResponse.of(article, addtionalInfo.isFollowing(), addtionalInfo.favorited(), addtionalInfo.favoritesCount());
    }

    public Article findBySlug(String slug) {
        return articleRepository.findBySlug(slug)
                                .orElseThrow(() -> new IllegalArgumentException(Error.ARTICLE_NOT_FOUND.getMessage()));
    }

    private record AddtionalInfo(boolean isFollowing, boolean favorited, int favoritesCount) {}
    private AddtionalInfo getArticleAdditionalInfo(AuthUser user, Article article) {
        List<Favorite> favoriteList = article.getFavoriteList();
        int favoritesCount = favoriteList.size();

        boolean isFollowing = false;
        boolean favorited = false;
        if (user.isAnonymous()) {
            return new AddtionalInfo(isFollowing, favorited, favoritesCount);
        }

        isFollowing = profileService.getFollow(user.getId(), article.getAuthor().getUsername())
                                    .isFollowing();
        favorited = favoriteList.stream()
                                .anyMatch(f -> f.getUser()
                                                .getId()
                                                .equals(user.getId()));

        return new AddtionalInfo(isFollowing, favorited, favoritesCount);
    }

    public List<ArticleServiceResponse> getArticles(AuthUser user, ArticleGetArticlesServiceRequest request) {
        String tag = request.getTag();
        String author = request.getAuthor();
        String favorited = request.getFavorited();
        Pageable pageable = request.getPageable();

        return articleRepository.findByArguments(tag, author, favorited, pageable)
                                .orElseThrow(() -> new IllegalArgumentException(Error.ARTICLE_NOT_FOUND.getMessage()))
                                .stream()
                                .map(article -> {
                                    AddtionalInfo addtionalInfo = getArticleAdditionalInfo(user, article);
                                    return ArticleServiceResponse.of(article,
                                            addtionalInfo.isFollowing(),
                                            addtionalInfo.favorited(),
                                            addtionalInfo.favoritesCount());
                                })
                                .toList();
    }

    public List<ArticleServiceResponse> getFeedArticles(AuthUser user, Pageable pageable) {
        List<Long> followeeIds = profileService.getFollwings(user.getId())
                                               .stream()
                                               .map(f -> f.getFollowee()
                                                          .getId())
                                               .toList();

        if(followeeIds.isEmpty()) {
            return List.of();
        }

        return articleRepository.findByAuthorIds(followeeIds, pageable)
                                .orElse(List.of())
                                .stream()
                                .map(article -> {
                                    AddtionalInfo addtionalInfo = getArticleAdditionalInfo(user, article);
                                    return ArticleServiceResponse.of(article,
                                            addtionalInfo.isFollowing(),
                                            addtionalInfo.favorited(),
                                            addtionalInfo.favoritesCount());
                                }).toList();
    }
}
