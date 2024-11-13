package io.hoon.realworld.api.service.article;

import io.hoon.realworld.api.service.article.request.ArticleCreateServiceRequest;
import io.hoon.realworld.api.service.article.request.ArticleUpdateServiceRequest;
import io.hoon.realworld.api.service.article.response.ArticleSingleResponse;
import io.hoon.realworld.api.service.user.UserService;
import io.hoon.realworld.domain.article.Article;
import io.hoon.realworld.domain.article.ArticleRepository;
import io.hoon.realworld.domain.user.User;
import io.hoon.realworld.exception.Error;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserService userService;

    @Transactional
    public ArticleSingleResponse createArticle(Long authorId, ArticleCreateServiceRequest request) {
        User author = userService.findById(authorId)
                                 .orElseThrow(() -> new IllegalArgumentException(Error.USER_NOT_FOUND.getMessage()));

        Article article = request.toEntity();
        article.setAuthorId(author);
        Article savedArticle = articleRepository.save(article);

        return ArticleSingleResponse.of(savedArticle, false);
    }

    @Transactional
    public ArticleSingleResponse updateArticle(long authorId, String slug, ArticleUpdateServiceRequest request) {
        Article article = findArticleBySlugAndValidateAuthor(slug, authorId);

        request.getTitle()
               .ifPresent(article::updateTitle);
        request.getDescription()
               .ifPresent(article::updateDescription);
        request.getBody()
               .ifPresent(article::updateBody);

        return ArticleSingleResponse.of(article, false);
    }

    @Transactional
    public void deleteArticle(Long authorId, String slug) {
        findArticleBySlugAndValidateAuthor(slug, authorId);

        int result = articleRepository.deleteBySlug(slug);
        if (result == 0) {
            throw new IllegalStateException(Error.FAILED_TO_DELETE.getMessage());
        }
    }

    private Article findArticleBySlugAndValidateAuthor(String slug, Long authorId) {
        Article article = articleRepository.findBySlug(slug)
                                           .orElseThrow(() -> new IllegalArgumentException(Error.ARTICLE_NOT_FOUND.getMessage()));

        Long getAuthorId = article.getAuthor()
                                  .getId();
        if (!getAuthorId.equals(authorId)) {
            throw new IllegalArgumentException(Error.INVALID_TOKEN.getMessage());
        }

        return article;
    }
}
