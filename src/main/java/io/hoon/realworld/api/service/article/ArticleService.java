package io.hoon.realworld.api.service.article;

import io.hoon.realworld.api.service.article.request.ArticleCreateServiceRequest;
import io.hoon.realworld.api.service.article.request.ArticleUpdateServiceRequest;
import io.hoon.realworld.api.service.article.response.ArticleSingleResponse;
import io.hoon.realworld.api.service.profile.ProfileService;
import io.hoon.realworld.api.service.profile.response.ProfileSingleResponse;
import io.hoon.realworld.domain.article.Article;
import io.hoon.realworld.domain.article.ArticleRepository;
import io.hoon.realworld.domain.article.favorite.Favorite;
import io.hoon.realworld.domain.article.favorite.FavoriteRepository;
import io.hoon.realworld.domain.user.User;
import io.hoon.realworld.exception.Error;
import io.hoon.realworld.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final FavoriteRepository favoriteRepository;
    private final ProfileService profileService;

    @Transactional
    public ArticleSingleResponse createArticle(AuthUser user, ArticleCreateServiceRequest request) {
        User author = user.toEntity();
        Article article = request.toEntity();
        article.setAuthor(author);
        article = articleRepository.save(article);

        return ArticleSingleResponse.of(article, false, false, 0);
    }

    @Transactional
    public ArticleSingleResponse updateArticle(AuthUser user, String slug, ArticleUpdateServiceRequest request) {
        Article article = findArticleBySlugAndValidateAuthor(user.getId(), slug);

        request.getTitle()
               .ifPresent(article::updateTitle);
        request.getDescription()
               .ifPresent(article::updateDescription);
        request.getBody()
               .ifPresent(article::updateBody);

        return ArticleSingleResponse.of(article, false, false, article.getFavoriteList()
                                                                      .size());
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
        Article article = articleRepository.findBySlug(slug)
                                           .orElseThrow(() -> new IllegalArgumentException(Error.ARTICLE_NOT_FOUND.getMessage()));

        Long getAuthorId = article.getAuthor()
                                  .getId();
        if (!getAuthorId.equals(authorId)) {
            throw new IllegalArgumentException(Error.INVALID_TOKEN.getMessage());
        }

        return article;
    }

    @Transactional
    public ArticleSingleResponse favoriteArticle(AuthUser user, String slug) {
        Article article = articleRepository.findBySlug(slug)
                                           .orElseThrow(() -> new IllegalArgumentException(Error.ARTICLE_NOT_FOUND.getMessage()));

        favoriteRepository.findByArticleIdAndUserId(article.getId(), user.getId())
                          .ifPresent(favorite -> {
                              throw new IllegalStateException(Error.ALREADY_FAVORITED.getMessage());
                          });

        Favorite favorite = favoriteRepository.save(Favorite.create(article, user.toEntity()));
        article.addFavorite(favorite);

        ProfileSingleResponse profile = profileService.get(user.getId(), article.getAuthor()
                                                                                .getUsername());

        List<Favorite> favoriteList = article.getFavoriteList();
        int favoritesCount = favoriteList.size();
        boolean favorited = favoriteList.stream()
                                        .anyMatch(f -> f.getUser()
                                                        .getId()
                                                        .equals(user.getId()));

        return ArticleSingleResponse.of(article, profile.isFollowing(), favorited, favoritesCount);
    }

    @Transactional
    public ArticleSingleResponse unfavoriteArticle(AuthUser user, String slug) {
        Article article = articleRepository.findBySlug(slug)
                                           .orElseThrow(() -> new IllegalArgumentException(Error.ARTICLE_NOT_FOUND.getMessage()));

        Favorite favorite = favoriteRepository.findByArticleIdAndUserId(article.getId(), user.getId())
                                              .orElseThrow(() -> new IllegalArgumentException(Error.NOT_FAVORITED.getMessage()));

        article.getFavoriteList()
               .remove(favorite);

        ProfileSingleResponse profile = profileService.get(user.getId(), article.getAuthor()
                                                                                .getUsername());

        List<Favorite> favoriteList = article.getFavoriteList();
        int favoritesCount = favoriteList.size();

        return ArticleSingleResponse.of(article, profile.isFollowing(), false, favoritesCount);
    }
}
