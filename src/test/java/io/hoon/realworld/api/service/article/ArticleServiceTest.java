package io.hoon.realworld.api.service.article;

import io.hoon.realworld.IntegrationTestSupport;
import io.hoon.realworld.api.service.article.request.ArticleCreateServiceRequest;
import io.hoon.realworld.api.service.article.response.ArticleSingleResponse;
import io.hoon.realworld.domain.article.tag.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleServiceTest extends IntegrationTestSupport {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private TagRepository tagRepository;

    @Test
    @DisplayName("아티클을 생성한다. 2개의 태그를 적용한다.")
    void createArticle() throws Exception {
        // Given
        ArticleCreateServiceRequest request = ArticleCreateServiceRequest.builder()
                                                                         .title("제목")
                                                                         .description("설명")
                                                                         .body("내용")
                                                                         .tagList(List.of("tag1", "tag2"))
                                                                         .build();

        // When
        ArticleSingleResponse response = articleService.createArticle(request);

        // Then
        assertThat(response)
                .extracting("title", "description", "body", "tagList")
                .contains("제목", "설명", "내용", response.getTagList());


        assertThat(response.getTagList()).hasSize(2)
                                         .contains("tag1", "tag2");
    }
}