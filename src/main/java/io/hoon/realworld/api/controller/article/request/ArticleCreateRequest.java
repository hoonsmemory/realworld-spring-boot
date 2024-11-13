package io.hoon.realworld.api.controller.article.request;

import com.fasterxml.jackson.annotation.JsonRootName;
import io.hoon.realworld.api.service.article.request.ArticleCreateServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonRootName("article")
public class ArticleCreateRequest {

    @NotEmpty(message = "The title is required.")
    private String title;

    @NotEmpty(message = "The description is required.")
    private String description;

    @NotEmpty(message = "The body is required.")
    private String body;
    private List<String> tagList;

    @Builder
    private ArticleCreateRequest(String title, String description, String body, List<String> tagList) {
        this.title = title;
        this.description = description;
        this.body = body;
        this.tagList = tagList;
    }

    public ArticleCreateServiceRequest toServiceRequest() {
        return ArticleCreateServiceRequest.builder()
                                          .title(title)
                                          .description(description)
                                          .body(body)
                                          .tagList(tagList)
                                          .build();
    }
}
