package io.hoon.realworld.api.controller.article.response;

import io.hoon.realworld.api.service.comment.response.CommentServiceResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class CommentMultiResponse {
    private final List<CommentServiceResponse> comments;
}
