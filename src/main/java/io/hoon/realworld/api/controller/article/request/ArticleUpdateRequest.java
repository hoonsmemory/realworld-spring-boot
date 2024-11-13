package io.hoon.realworld.api.controller.article.request;

import com.fasterxml.jackson.annotation.JsonRootName;
import io.hoon.realworld.api.service.article.request.ArticleUpdateServiceRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@NoArgsConstructor
@JsonRootName("article")
public class ArticleUpdateRequest {

    private String title;
    private String description;
    private String body;

    @Builder
    private ArticleUpdateRequest(String title, String description, String body) {
        this.title = title;
        this.description = description;
        this.body = body;
    }

    public ArticleUpdateServiceRequest toServiceRequest() {
        return ArticleUpdateServiceRequest.builder()
                                          .title(Optional.ofNullable(title))
                                          .description(Optional.ofNullable(description))
                                          .body(Optional.ofNullable(body))
                                          .build();
    }
}
