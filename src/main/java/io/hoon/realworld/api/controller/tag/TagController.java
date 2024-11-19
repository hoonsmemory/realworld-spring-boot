package io.hoon.realworld.api.controller.tag;

import io.hoon.realworld.api.controller.tag.response.TagMultiResponse;
import io.hoon.realworld.api.service.tag.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TagController {

    private final TagService tagService;

    @GetMapping("/api/tags")
    public TagMultiResponse getTags() {
        return new TagMultiResponse(tagService.getTags());
    }
}
