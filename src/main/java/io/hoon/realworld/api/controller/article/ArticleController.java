package io.hoon.realworld.api.controller.article;

import io.hoon.realworld.api.controller.article.request.ArticleCreateRequest;
import io.hoon.realworld.api.service.article.ArticleService;
import io.hoon.realworld.api.service.article.response.ArticleSingleResponse;
import io.hoon.realworld.domain.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/api/articles")
    public ArticleSingleResponse createArticle(User user, @Valid @RequestBody ArticleCreateRequest request) {
        ArticleSingleResponse article = articleService.createArticle(request.toServiceRequest());
        return article;
    }
}
