package io.hoon.realworld.api.service.comment.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.hoon.realworld.api.service.profile.response.ProfileServiceResponse;
import io.hoon.realworld.domain.article.comment.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentServiceResponse {
    private long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String body;
    @JsonProperty("author")
    private ProfileServiceResponse profileServiceResponse;

    @Builder
    private CommentServiceResponse(long id, LocalDateTime createdAt, LocalDateTime updatedAt, String body,
                                   ProfileServiceResponse profileServiceResponse) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.body = body;
        this.profileServiceResponse = profileServiceResponse;
    }

    public static CommentServiceResponse of(Comment comment, boolean isFollowing) {
        return CommentServiceResponse.builder()
                                     .id(comment.getId())
                                     .createdAt(comment.getCreatedAt())
                                     .updatedAt(comment.getUpdatedAt())
                                     .body(comment.getBody())
                                     .profileServiceResponse(ProfileServiceResponse.of(comment.getAuthor(), isFollowing))
                                     .build();
    }
}
