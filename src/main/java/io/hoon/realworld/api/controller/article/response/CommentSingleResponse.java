package io.hoon.realworld.api.controller.article.response;

import io.hoon.realworld.api.service.comment.response.CommentServiceResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CommentSingleResponse {
    private final CommentServiceResponse comment;
}
