package io.hoon.realworld.api.controller.article.response;

import io.hoon.realworld.api.service.article.response.ArticleServiceResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ArticleMultiResponse {
    private List<ArticleServiceResponse> articles;
    private int articlesCount;

    @Builder
    private ArticleMultiResponse(List<ArticleServiceResponse> articleServiceResponse, int articlesCount) {
        this.articles = articleServiceResponse;
        this.articlesCount = articlesCount;
    }

    public static ArticleMultiResponse of(List<ArticleServiceResponse> articles) {
        return ArticleMultiResponse.builder()
                                   .articleServiceResponse(articles)
                                   .articlesCount(articles.size())
                                   .build();
    }
}
