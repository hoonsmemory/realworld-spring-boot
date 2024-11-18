package io.hoon.realworld.api.controller.article;

import io.hoon.realworld.api.controller.article.request.ArticleCreateRequest;
import io.hoon.realworld.api.controller.article.request.ArticleUpdateRequest;
import io.hoon.realworld.api.controller.article.response.ArticleMultiResponse;
import io.hoon.realworld.api.controller.article.response.ArticleSingleResponse;
import io.hoon.realworld.api.service.article.ArticleService;
import io.hoon.realworld.api.service.article.request.ArticleGetArticlesServiceRequest;
import io.hoon.realworld.security.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/api/articles")
    public ArticleSingleResponse createArticle(AuthUser user, @Valid @RequestBody ArticleCreateRequest request) {
        return new ArticleSingleResponse(articleService.createArticle(user, request.toServiceRequest()));
    }

    @PutMapping("/api/articles/{slug}")
    public ArticleSingleResponse updateArticle(AuthUser user, @PathVariable String slug, @RequestBody ArticleUpdateRequest request) {
        return new ArticleSingleResponse(articleService.updateArticle(user, slug, request.toServiceRequest()));
    }

    @DeleteMapping("/api/articles/{slug}")
    public void deleteArticle(AuthUser user, @PathVariable String slug) {
        articleService.deleteArticle(user, slug);
    }

    @PostMapping("/api/articles/{slug}/favorite")
    public ArticleSingleResponse favoriteArticle(AuthUser user, @PathVariable String slug) {
        return new ArticleSingleResponse(articleService.favoriteArticle(user, slug));
    }

    @DeleteMapping("/api/articles/{slug}/favorite")
    public ArticleSingleResponse unfavoriteArticle(AuthUser user, @PathVariable String slug) {
        return new ArticleSingleResponse(articleService.unfavoriteArticle(user, slug));
    }

    @GetMapping("/api/articles/{slug}")
    public ArticleSingleResponse getArticle(AuthUser user, @PathVariable String slug) {
        return new ArticleSingleResponse(articleService.getArticle(user, slug));
    }

    @GetMapping("/api/articles")
    public ArticleMultiResponse getArticles(AuthUser user,
                                            @RequestParam(value = "tag", required = false) String tag,
                                            @RequestParam(value = "author", required = false) String author,
                                            @RequestParam(value = "favorited", required = false) String favorited,
                                            @RequestParam(value = "limit", required = false, defaultValue = "0") int offset,
                                            @RequestParam(value = "offset", required = false, defaultValue = "20") int limit) {

        return ArticleMultiResponse.of(articleService.getArticles(user, ArticleGetArticlesServiceRequest.builder()
                                                                                                        .tag(tag)
                                                                                                        .author(author)
                                                                                                        .favorited(favorited)
                                                                                                        .pageable(PageRequest.of(offset, limit))
                                                                                                        .build()));
    }

    @GetMapping("/api/articles/feed")
    public ArticleMultiResponse getFeedArticles(AuthUser user,
                                                @RequestParam(value = "limit", required = false, defaultValue = "0") int offset,
                                                @RequestParam(value = "offset", required = false, defaultValue = "20") int limit) {
        return ArticleMultiResponse.of(articleService.getFeedArticles(user, PageRequest.of(offset, limit)));
    }
}
