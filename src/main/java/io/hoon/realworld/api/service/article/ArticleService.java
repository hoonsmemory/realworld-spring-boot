package io.hoon.realworld.api.service.article;

import io.hoon.realworld.api.service.article.request.ArticleCreateServiceRequest;
import io.hoon.realworld.api.service.article.request.ArticleUpdateServiceRequest;
import io.hoon.realworld.api.service.article.response.ArticleSingleResponse;
import io.hoon.realworld.domain.article.Article;
import io.hoon.realworld.domain.article.ArticleRepository;
import io.hoon.realworld.exception.Error;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    @Transactional
    public ArticleSingleResponse createArticle(ArticleCreateServiceRequest request) {
        Article article = request.toEntity();
        Article savedArticle = articleRepository.save(article);

        return ArticleSingleResponse.of(savedArticle);
    }

    @Transactional
    public ArticleSingleResponse updateArticle(String slug, ArticleUpdateServiceRequest request) {
        Article article = articleRepository.findBySlug(slug)
                                           .orElseThrow(() -> new IllegalArgumentException(Error.ARTICLE_NOT_FOUND.getMessage()));

        request.getTitle().ifPresent(article::updateTitle);
        request.getDescription().ifPresent(article::updateDescription);
        request.getBody().ifPresent(article::updateBody);

        return ArticleSingleResponse.of(article);
    }
}
