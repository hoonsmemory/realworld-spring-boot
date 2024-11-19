package io.hoon.realworld.api.service.tag;

import io.hoon.realworld.domain.article.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TagService {

    private final TagRepository tagRepository;

    public List<String> getTags() {
        return tagRepository.findAll().stream().map(tag -> tag.getName()).toList();
    }
}