package io.hoon.realworld.api.service.article.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
public class ArticleGetArticlesServiceRequest {
    private String tag;
    private String author;
    private String favorited;
    private Pageable pageable;

    @Builder
    private ArticleGetArticlesServiceRequest(String tag, String author, String favorited, Pageable pageable) {
        this.tag = tag;
        this.author = author;
        this.favorited = favorited;
        this.pageable = pageable;
    }
}
