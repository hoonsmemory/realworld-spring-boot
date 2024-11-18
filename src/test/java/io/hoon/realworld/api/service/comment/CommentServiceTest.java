package io.hoon.realworld.api.service.comment;

import io.hoon.realworld.IntegrationTestSupport;
import io.hoon.realworld.api.service.article.ArticleService;
import io.hoon.realworld.api.service.article.request.ArticleCreateServiceRequest;
import io.hoon.realworld.api.service.comment.response.CommentServiceResponse;
import io.hoon.realworld.api.service.user.UserService;
import io.hoon.realworld.api.service.user.request.UserSignUpServiceRequest;
import io.hoon.realworld.domain.user.User;
import io.hoon.realworld.security.AuthUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommentServiceTest extends IntegrationTestSupport {

    @Autowired
    private CommentService commentService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        //-- 유저1 회원가입
        userService.signUp(UserSignUpServiceRequest.builder()
                                                   .email("hoon@email.com")
                                                   .username("hoon")
                                                   .password("1234")
                                                   .build());

        //-- 유저1 아티클 생성
        User user = userService.findByEmail("hoon@email.com").get();
        AuthUser authUser = AuthUser.builder()
                           .id(user.getId())
                           .email(user.getEmail())
                           .username(user.getUsername())
                           .bio(user.getBio())
                           .image(user.getImage())
                           .build();
        articleService.createArticle(authUser, ArticleCreateServiceRequest.builder()
                                                                          .title("제목")
                                                                          .description("설명")
                                                                          .body("내용")
                                                                          .tagList(List.of("tag1", "tag2"))
                                                                          .build());

        //-- 유저2 회원가입
        userService.signUp(UserSignUpServiceRequest.builder()
                                                   .email("emily@email.com")
                                                   .username("emily")
                                                   .password("1234")
                                                   .build());
    }

    @Test
    @DisplayName("코멘트를 생성한다.")
    void createComment() throws Exception {
        // Given
        User user = userService.findByEmail("emily@email.com").get();
        AuthUser authUser = AuthUser.builder()
                           .id(user.getId())
                           .email(user.getEmail())
                           .username(user.getUsername())
                           .bio(user.getBio())
                           .image(user.getImage())
                           .build();

        // When
        CommentServiceResponse response = commentService.createComment(authUser, "제목", "코멘트 테스트");

        // Then
    	assertThat(response.getBody()).isEqualTo("코멘트 테스트");
        assertThat((response.getProfileServiceResponse().getUsername())).isEqualTo(authUser.getUsername());
    }

    @Test
    @DisplayName("아티클의 코멘트를 조회한다.")
    void getComments() throws Exception {
        // Given
        //-- 코멘트 생성
        User user = userService.findByEmail("emily@email.com").get();
        AuthUser authUser = AuthUser.builder()
                                    .id(user.getId())
                                    .email(user.getEmail())
                                    .username(user.getUsername())
                                    .bio(user.getBio())
                                    .image(user.getImage())
                                    .build();

        commentService.createComment(authUser, "제목", "코멘트 테스트");

        // When
        List<CommentServiceResponse> response = commentService.getComments(authUser, "제목");

        // Then
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getBody()).isEqualTo("코멘트 테스트");
        assertThat(response.get(0).getProfileServiceResponse().getUsername()).isEqualTo(authUser.getUsername());
    }

    @Test
    @DisplayName("코멘트를 삭제한다.")
    void deleteComment() throws Exception {
        // Given
        User user = userService.findByEmail("emily@email.com").get();
        AuthUser authUser = AuthUser.builder()
                                    .id(user.getId())
                                    .email(user.getEmail())
                                    .username(user.getUsername())
                                    .bio(user.getBio())
                                    .image(user.getImage())
                                    .build();

        CommentServiceResponse commentResponse = commentService.createComment(authUser, "제목", "코멘트 테스트");

        // When
        commentService.deleteComment(authUser, "제목", commentResponse.getId());

        // Then
        List<CommentServiceResponse> response = commentService.getComments(authUser, "제목");
        assertThat(response).isEmpty();
    }
}