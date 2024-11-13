package io.hoon.realworld.api.service.article;

import io.hoon.realworld.IntegrationTestSupport;
import io.hoon.realworld.api.service.article.request.ArticleCreateServiceRequest;
import io.hoon.realworld.api.service.article.request.ArticleUpdateServiceRequest;
import io.hoon.realworld.api.service.article.response.ArticleSingleResponse;
import io.hoon.realworld.domain.article.Article;
import io.hoon.realworld.domain.article.ArticleRepository;
import io.hoon.realworld.domain.article.tag.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleServiceTest extends IntegrationTestSupport {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    @DisplayName("아티클을 생성한다. 2개의 태그를 적용한다.")
    @Transactional
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

    @Test
    @DisplayName("아티클을 수정한다.")
    @Transactional
    void test() throws Exception {
        // Given
        // -- 아티클 생성
        articleService.createArticle(ArticleCreateServiceRequest.builder()
                                                                .title("제목")
                                                                .description("설명")
                                                                .body("내용")
                                                                .tagList(List.of("tag1", "tag2"))
                                                                .build());

        // -- 아티클 수정
        ArticleUpdateServiceRequest request = ArticleUpdateServiceRequest.builder()
                                                                         .title(Optional.of("제목 변경"))
                                                                         .description(Optional.of("설명 변경"))
                                                                         .body(Optional.of("내용 변경"))
                                                                         .build();
        // When
        ArticleSingleResponse response = articleService.updateArticle("제목", request);

        // Then
        assertThat(response)
                .extracting("title", "description", "body")
                .contains("제목 변경", "설명 변경", "내용 변경");

        Article article = articleRepository.findBySlug("제목-변경").get();
        assertThat(article).isNotNull();
    }
}