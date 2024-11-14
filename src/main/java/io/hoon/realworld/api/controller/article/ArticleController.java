package io.hoon.realworld.api.controller.article;

import io.hoon.realworld.api.controller.article.request.ArticleCreateRequest;
import io.hoon.realworld.api.controller.article.request.ArticleUpdateRequest;
import io.hoon.realworld.api.service.article.ArticleService;
import io.hoon.realworld.api.service.article.response.ArticleSingleResponse;
import io.hoon.realworld.domain.user.User;
import io.hoon.realworld.security.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/api/articles")
    public ArticleSingleResponse createArticle(AuthUser user, @Valid @RequestBody ArticleCreateRequest request) {
        return articleService.createArticle(user, request.toServiceRequest());
    }

    @PutMapping("/api/articles/{slug}")
    public ArticleSingleResponse updateArticle(AuthUser user, @PathVariable String slug, @RequestBody ArticleUpdateRequest request) {
        return articleService.updateArticle(user, slug, request.toServiceRequest());
    }

    @DeleteMapping("/api/articles/{slug}")
    public void deleteArticle(AuthUser user, @PathVariable String slug) {
        articleService.deleteArticle(user, slug);
    }
}
