package io.hoon.realworld.api.controller.article;

import io.hoon.realworld.IntegrationTestSupport;
import io.hoon.realworld.api.controller.article.request.ArticleCreateRequest;
import io.hoon.realworld.api.service.user.UserService;
import io.hoon.realworld.api.service.user.request.UserLoginServiceRequest;
import io.hoon.realworld.api.service.user.request.UserSignUpServiceRequest;
import io.hoon.realworld.api.service.user.response.UserSingleResponse;
import io.hoon.realworld.domain.article.ArticleRepository;
import io.hoon.realworld.domain.article.tag.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ArticleControllerTestWithSecurity extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private UserSingleResponse response = null;

    @BeforeEach
    void setUp() {
        //-- 유저1 회원가입
        UserSignUpServiceRequest userSignUpServiceRequest1 = UserSignUpServiceRequest.builder()
                                                                                     .email("hoon@email.com")
                                                                                     .username("hoon")
                                                                                     .password("1234")
                                                                                     .build();

        userService.signUp(userSignUpServiceRequest1);

        //-- 유저1 로그인
        UserLoginServiceRequest loginRequest = UserLoginServiceRequest.builder()
                                                                      .email("hoon@email.com")
                                                                      .password("1234")
                                                                      .build();

        response = userService.login(loginRequest);
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
                       .header("Authorization", "Token " + response.getToken())
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
                       .header("Authorization", "Token " + response.getToken())
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
                       .header("Authorization", "Token " + response.getToken())
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
                       .header("Authorization", "Token " + response.getToken())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andDo(print())
               .andExpect(status().isBadRequest());
    }

}