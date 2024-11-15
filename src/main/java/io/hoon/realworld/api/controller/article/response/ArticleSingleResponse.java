package io.hoon.realworld.api.controller.article.response;

import io.hoon.realworld.api.service.article.response.ArticleServiceResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ArticleSingleResponse {
    private final ArticleServiceResponse article;
}
