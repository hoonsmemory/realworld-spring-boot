package io.hoon.realworld.api.service.article.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import io.hoon.realworld.api.service.profile.response.ProfileServiceResponse;
import io.hoon.realworld.domain.article.Article;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@JsonRootName("article")
public class ArticleServiceResponse {
    private String slug;
    private String title;
    private String description;
    private String body;
    private List<String> tagList;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean favorited;
    private int favoritesCount;

    @JsonProperty("author")
    private ProfileServiceResponse profileServiceResponse;

    @Builder
    private ArticleServiceResponse(String slug, String title, String description, String body, List<String> tagList,
                                   LocalDateTime createdAt, LocalDateTime updatedAt, ProfileServiceResponse profileServiceResponse,
                                   boolean favorited, int favoritesCount) {
        this.slug = slug;
        this.title = title;
        this.description = description;
        this.body = body;
        this.tagList = tagList;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.profileServiceResponse = profileServiceResponse;
        this.favorited = favorited;
        this.favoritesCount = favoritesCount;
    }

    public static ArticleServiceResponse of(Article article, boolean isFollow, boolean isFavorite, int favoritesCount) {
        List<String> tagList = Optional.ofNullable(article.getTagList())
                                       .orElse(Collections.emptyList())
                                       .stream()
                                       .map(tag -> tag.getTag().getName())
                                       .toList();

        return ArticleServiceResponse.builder()
                                     .slug(article.getSlug())
                                     .title(article.getTitle())
                                     .description(article.getDescription())
                                     .body(article.getBody())
                                     .tagList(tagList)
                                     .createdAt(article.getCreatedAt())
                                     .updatedAt(article.getUpdatedAt())
                                     .favorited(isFavorite)
                                     .favoritesCount(favoritesCount)
                                     .profileServiceResponse(ProfileServiceResponse.of(article.getAuthor(), isFollow))
                                     .build();
    }
}
