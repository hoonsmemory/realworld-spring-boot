package io.hoon.realworld.api.controller.tag.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class TagMultiResponse {
    private final List<String> tags;
}
