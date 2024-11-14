package io.hoon.realworld.api.service.article;

import io.hoon.realworld.api.service.article.request.ArticleCreateServiceRequest;
import io.hoon.realworld.api.service.article.request.ArticleUpdateServiceRequest;
import io.hoon.realworld.api.service.article.response.ArticleSingleResponse;
import io.hoon.realworld.api.service.profile.ProfileService;
import io.hoon.realworld.api.service.profile.response.ProfileSingleResponse;
import io.hoon.realworld.api.service.user.UserService;
import io.hoon.realworld.domain.article.Article;
import io.hoon.realworld.domain.article.ArticleRepository;
import io.hoon.realworld.domain.article.favorite.Favorite;
import io.hoon.realworld.domain.article.favorite.FavoriteRepository;
import io.hoon.realworld.domain.user.User;
import io.hoon.realworld.exception.Error;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserService userService;
    private final FavoriteRepository favoriteRepository;
    private final ProfileService profileService;

    @Transactional
    public ArticleSingleResponse createArticle(Long authorId, ArticleCreateServiceRequest request) {
        User author = userService.findById(authorId)
                                 .orElseThrow(() -> new IllegalArgumentException(Error.USER_NOT_FOUND.getMessage()));

        Article article = request.toEntity();
        article.setAuthor(author);
        article = articleRepository.save(article);

        return ArticleSingleResponse.of(article, false, false, 0);
    }

    @Transactional
    public ArticleSingleResponse updateArticle(long authorId, String slug, ArticleUpdateServiceRequest request) {
        Article article = findArticleBySlugAndValidateAuthor(authorId, slug);

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
    public void deleteArticle(Long authorId, String slug) {
        findArticleBySlugAndValidateAuthor(authorId, slug);

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
    public ArticleSingleResponse favoriteArticle(Long userId, String slug) {
        Article article = articleRepository.findBySlug(slug)
                                           .orElseThrow(() -> new IllegalArgumentException(Error.ARTICLE_NOT_FOUND.getMessage()));

        User user = userService.findById(userId)
                               .orElseThrow(() -> new IllegalArgumentException(Error.USER_NOT_FOUND.getMessage()));

        favoriteRepository.findByArticleIdAndUserId(article.getId(), user.getId())
                          .ifPresent(favorite -> {
                              throw new IllegalStateException(Error.ALREADY_FAVORITED.getMessage());
                          });

        Favorite favorite = favoriteRepository.save(Favorite.create(article, user));
        article.addFavorite(favorite);

        ProfileSingleResponse profile = profileService.get(user.getId(), article.getAuthor()
                                                                                .getUsername());

        List<Favorite> favoriteList = article.getFavoriteList();
        int favoritesCount = favoriteList.size();
        boolean favorited = favoriteList.stream()
                                        .anyMatch(f -> f.getUser()
                                                        .getId()
                                                        .equals(userId));

        return ArticleSingleResponse.of(article, profile.isFollowing(), favorited, favoritesCount);
    }
}
