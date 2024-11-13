package io.hoon.realworld.api.service.article.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import io.hoon.realworld.api.service.profile.response.ProfileSingleResponse;
import io.hoon.realworld.domain.article.Article;
import io.hoon.realworld.domain.article.tag.Tag;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@JsonRootName("article")
public class ArticleSingleResponse {
    private String title;
    private String description;
    private String body;
    private List<String> tagList;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @JsonProperty("author")
    private ProfileSingleResponse profileSingleResponse;

    @Builder
    private ArticleSingleResponse(String title, String description, String body, List<String> tagList, LocalDateTime createdAt, LocalDateTime updatedAt, ProfileSingleResponse profileSingleResponse) {
        this.title = title;
        this.description = description;
        this.body = body;
        this.tagList = tagList;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.profileSingleResponse = profileSingleResponse;
    }

    public static ArticleSingleResponse of(Article savedArticle, boolean isFollow) {
        List<String> tagList = Optional.ofNullable(savedArticle.getTagList())
                                       .orElse(Collections.emptyList())
                                       .stream()
                                       .map(Tag::getName)
                                       .toList();


        return ArticleSingleResponse.builder()
                                    .title(savedArticle.getTitle())
                                    .description(savedArticle.getDescription())
                                    .body(savedArticle.getBody())
                                    .tagList(tagList)
                                    .createdAt(savedArticle.getCreatedAt())
                                    .updatedAt(savedArticle.getUpdatedAt())
                                    .profileSingleResponse(ProfileSingleResponse.of(savedArticle.getAuthor(), isFollow))
                                    .build();
    }
}
