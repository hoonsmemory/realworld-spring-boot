package io.hoon.realworld.api.service.article.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import io.hoon.realworld.api.service.profile.response.ProfileSingleResponse;
import io.hoon.realworld.domain.article.Article;
import io.hoon.realworld.domain.article.favorite.Favorite;
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
    private ProfileSingleResponse profileSingleResponse;

    @Builder
    private ArticleSingleResponse(String slug, String title, String description, String body, List<String> tagList,
                                  LocalDateTime createdAt, LocalDateTime updatedAt, ProfileSingleResponse profileSingleResponse,
                                  boolean favorited, int favoritesCount) {
        this.slug = slug;
        this.title = title;
        this.description = description;
        this.body = body;
        this.tagList = tagList;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.profileSingleResponse = profileSingleResponse;
        this.favorited = favorited;
        this.favoritesCount = favoritesCount;
    }

    public static ArticleSingleResponse of(Article article, boolean isFollow, boolean isFavorite, int favoritesCount) {
        List<String> tagList = Optional.ofNullable(article.getTagList())
                                       .orElse(Collections.emptyList())
                                       .stream()
                                       .map(Tag::getName)
                                       .toList();

        return ArticleSingleResponse.builder()
                                    .slug(article.getSlug())
                                    .title(article.getTitle())
                                    .description(article.getDescription())
                                    .body(article.getBody())
                                    .tagList(tagList)
                                    .createdAt(article.getCreatedAt())
                                    .updatedAt(article.getUpdatedAt())
                                    .favorited(isFavorite)
                                    .favoritesCount(favoritesCount)
                                    .profileSingleResponse(ProfileSingleResponse.of(article.getAuthor(), isFollow))
                                    .build();
    }
}
