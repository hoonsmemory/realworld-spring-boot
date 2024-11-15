package io.hoon.realworld.api.service.article.request;

import io.hoon.realworld.domain.article.Article;
import io.hoon.realworld.domain.article.tag.Tag;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ArticleCreateServiceRequest {

    private String title;
    private String description;
    private String body;
    private List<String> tagList;

    @Builder
    private ArticleCreateServiceRequest(String title, String description, String body, List<String> tagList) {
        this.title = title;
        this.description = description;
        this.body = body;
        this.tagList = tagList;
    }

    public Article toArticleEntity() {
        return Article.builder()
                      .title(title)
                      .description(description)
                      .body(body)
                      .build();
    }

    public List<Tag> toTagEntityList() {
        return tagList.stream()
                      .map(name -> Tag.builder()
                                     .name(name)
                                     .build())
                      .toList();
    }
}
