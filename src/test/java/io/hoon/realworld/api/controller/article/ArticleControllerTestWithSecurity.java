package io.hoon.realworld.api.controller.article;

import io.hoon.realworld.IntegrationTestSupport;
import io.hoon.realworld.api.controller.article.request.ArticleCreateRequest;
import io.hoon.realworld.api.controller.article.request.ArticleUpdateRequest;
import io.hoon.realworld.api.service.article.ArticleService;
import io.hoon.realworld.api.service.user.UserService;
import io.hoon.realworld.api.service.user.request.UserLoginServiceRequest;
import io.hoon.realworld.api.service.user.request.UserSignUpServiceRequest;
import io.hoon.realworld.domain.article.ArticleRepository;
import io.hoon.realworld.domain.user.User;
import io.hoon.realworld.security.AuthUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ArticleControllerTestWithSecurity extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

    private String token;

    private AuthUser authUser;

    @BeforeEach
    void setUp() {
        //-- 유저1 회원가입
        String email = "hoon@email.com";
        String username = "hoon";
        String password = "1234";

        userService.signUp(UserSignUpServiceRequest.builder()
                                                   .email(email)
                                                   .username(username)
                                                   .password(password)
                                                   .build());

        //-- 유저1 로그인
        token = userService.login(UserLoginServiceRequest.builder()
                                                         .email(email)
                                                         .password(password)
                                                         .build())
                           .getToken();

        //-- user 조회
        User user = userService.findByEmail(email).get();
        authUser = AuthUser.builder()
                           .id(user.getId())
                           .email(user.getEmail())
                           .username(user.getUsername())
                           .bio(user.getBio())
                           .image(user.getImage())
                           .build();
    }

    @Test
    @DisplayName("아티클을 생성한다.")
    @Transactional
    void createArticle() throws Exception {
        // Given
        ArticleCreateRequest request = ArticleCreateRequest.builder()
                                                           .title("제목")
                                                           .description("설명")
                                                           .body("내용")
                                                           .tagList(List.of("tag1", "tag2"))
                                                           .build();

        // When // Then
        mockMvc.perform(post("/api/articles")
                       .header("Authorization", "Token " + token)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.article.title").value("제목"))
               .andExpect(jsonPath("$.article.description").value("설명"))
               .andExpect(jsonPath("$.article.body").value("내용"))
               .andExpect(jsonPath("$.article.tagList").isNotEmpty())
               .andExpect(jsonPath("$.article.tagList").isArray())
               .andExpect(jsonPath("$.article.tagList[0]").value("tag1"))
               .andExpect(jsonPath("$.article.tagList[1]").value("tag2"));
    }

    @Test
    @DisplayName("아티클을 생성할 떄 타이틀은 필수다.")
    @Transactional
    void createArticleWithoutTitle() throws Exception {
        // Given
        ArticleCreateRequest request = ArticleCreateRequest.builder()
                                                           .description("설명")
                                                           .body("내용")
                                                           .tagList(List.of("tag1", "tag2"))
                                                           .build();

        // When // Then
        mockMvc.perform(post("/api/articles")
                       .header("Authorization", "Token " + token)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andDo(print())
               .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("아티클을 생성할 떄 설명은 필수다.")
    @Transactional
    void createArticleWithoutDescription() throws Exception {
        // Given
        ArticleCreateRequest request = ArticleCreateRequest.builder()
                                                           .title("제목")
                                                           .body("내용")
                                                           .tagList(List.of("tag1", "tag2"))
                                                           .build();

        // When // Then
        mockMvc.perform(post("/api/articles")
                       .header("Authorization", "Token " + token)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andDo(print())
               .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("아티클을 생성할 떄 내용은 필수다.")
    @Transactional
    void createArticleWithoutBody() throws Exception {
        // Given
        ArticleCreateRequest request = ArticleCreateRequest.builder()
                                                           .title("제목")
                                                           .description("설명")
                                                           .tagList(List.of("tag1", "tag2"))
                                                           .build();

        // When // Then
        mockMvc.perform(post("/api/articles")
                       .header("Authorization", "Token " + token)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andDo(print())
               .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("아티클의 제목, 설명, 내용을 변경한다.")
    @Transactional
    void updateArticle() throws Exception {
        // Given
        // -- 아티클 생성
        ArticleCreateRequest articleCreateRequest = ArticleCreateRequest.builder()
                                                                        .title("제목")
                                                                        .description("설명")
                                                                        .body("내용")
                                                                        .tagList(List.of("tag1", "tag2"))
                                                                        .build();
        articleService.createArticle(authUser, articleCreateRequest.toServiceRequest());

        // -- 아티클 변경
        ArticleUpdateRequest articleUpdateRequest = ArticleUpdateRequest.builder()
                                                                        .title("제목 변경")
                                                                        .description("설명 변경")
                                                                        .body("내용 변경")
                                                                        .build();

        // When // Then
        mockMvc.perform(put("/api/articles/제목")
                       .header("Authorization", "Token " + token)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(articleUpdateRequest)))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.article.title").value("제목 변경"))
               .andExpect(jsonPath("$.article.description").value("설명 변경"))
               .andExpect(jsonPath("$.article.body").value("내용 변경"))
               .andExpect(jsonPath("$.article.tagList").isNotEmpty());
    }

    @Test
    @DisplayName("아티클을 삭제한다.")
    @Transactional
    void deleteArticle() throws Exception {
        // Given
        // -- 아티클 생성
        ArticleCreateRequest articleCreateRequest = ArticleCreateRequest.builder()
                                                                        .title("아티클 제목")
                                                                        .description("설명")
                                                                        .body("내용")
                                                                        .tagList(List.of("tag1", "tag2"))
                                                                        .build();
        articleService.createArticle(authUser, articleCreateRequest.toServiceRequest());

        // When // Then
        mockMvc.perform(delete("/api/articles/아티클-제목")
                       .header("Authorization", "Token " + token)
                       .contentType(MediaType.APPLICATION_JSON))
               .andDo(print())
               .andExpect(status().isOk());

        assertThat(articleRepository.findBySlug("아티클-제목")).isEmpty();
    }

}