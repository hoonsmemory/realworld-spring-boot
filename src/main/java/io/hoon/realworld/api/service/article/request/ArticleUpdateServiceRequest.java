package io.hoon.realworld.api.service.article.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;


@Getter
@NoArgsConstructor
public class ArticleUpdateServiceRequest {

    private Optional<String> title;
    private Optional<String> description;
    private Optional<String> body;

    @Builder
    private ArticleUpdateServiceRequest(Optional<String> title, Optional<String> description, Optional<String> body) {
        this.title = title;
        this.description = description;
        this.body = body;
    }
}
