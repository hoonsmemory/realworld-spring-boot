package io.hoon.realworld.api.service.article;

import io.hoon.realworld.IntegrationTestSupport;
import io.hoon.realworld.api.service.article.request.ArticleCreateServiceRequest;
import io.hoon.realworld.api.service.article.request.ArticleGetArticlesServiceRequest;
import io.hoon.realworld.api.service.article.request.ArticleUpdateServiceRequest;
import io.hoon.realworld.api.service.article.response.ArticleServiceResponse;
import io.hoon.realworld.api.service.profile.ProfileService;
import io.hoon.realworld.api.service.user.UserService;
import io.hoon.realworld.api.service.user.request.UserLoginServiceRequest;
import io.hoon.realworld.api.service.user.request.UserSignUpServiceRequest;
import io.hoon.realworld.api.service.user.response.UserServiceResponse;
import io.hoon.realworld.domain.article.Article;
import io.hoon.realworld.domain.article.ArticleRepository;
import io.hoon.realworld.domain.user.User;
import io.hoon.realworld.exception.Error;
import io.hoon.realworld.security.AuthUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ArticleServiceTest extends IntegrationTestSupport {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProfileService profileService;

    private AuthUser authUser;

    @BeforeEach
    void setUp() {
        //-- 유저1 회원가입
        userService.signUp(UserSignUpServiceRequest.builder()
                                                   .email("hoon@email.com")
                                                   .username("hoon")
                                                   .password("1234")
                                                   .build());

        //-- 유저1 로그인
        userService.login(UserLoginServiceRequest.builder()
                                                 .email("hoon@email.com")
                                                 .password("1234")
                                                 .build());

        //-- user 조회
        User user = userService.findByEmail("hoon@email.com").get();
        authUser = AuthUser.builder()
                           .id(user.getId())
                           .email(user.getEmail())
                           .username(user.getUsername())
                           .bio(user.getBio())
                           .image(user.getImage())
                           .build();
    }

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
        ArticleServiceResponse response = articleService.createArticle(authUser, request);

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
        articleService.createArticle(authUser, ArticleCreateServiceRequest.builder()
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
        ArticleServiceResponse response = articleService.updateArticle(authUser, "제목", request);

        // Then
        assertThat(response)
                .extracting("title", "description", "body")
                .contains("제목 변경", "설명 변경", "내용 변경");

        Article article = articleRepository.findBySlug("제목-변경")
                                           .get();
        assertThat(article).isNotNull();
    }

    @Test
    @DisplayName("아티클을 삭제한다.")
    @Transactional
    void deleteArticle() throws Exception {
        // Given
        // -- 아티클 생성
        ArticleServiceResponse article = articleService.createArticle(authUser, ArticleCreateServiceRequest.builder()
                                                                                                           .title("아티클 제목")
                                                                                                           .description("설명")
                                                                                                           .body("내용")
                                                                                                           .tagList(List.of("tag1", "tag2"))
                                                                                                           .build());

        // When
        articleService.deleteArticle(authUser, article.getSlug());

        // Then
        articleRepository.findBySlug(article.getSlug())
                         .ifPresent(a -> {
                             throw new IllegalStateException("The article was not deleted.");
                         });
    }

    @Test
    @DisplayName("어느 아티클을 즐겨찾기한다.")
    @Transactional
    void favoriteArticle() throws Exception {
        // Given
        // -- 아티클 생성
        ArticleServiceResponse article = articleService.createArticle(authUser, ArticleCreateServiceRequest.builder()
                                                                                                           .title("아티클 제목")
                                                                                                           .description("설명")
                                                                                                           .body("내용")
                                                                                                           .tagList(List.of("tag1", "tag2"))
                                                                                                           .build());

        UserServiceResponse userServiceResponse = userService.signUp(UserSignUpServiceRequest.builder()
                                                                                             .email("emily@email.com")
                                                                                             .username("emily")
                                                                                             .password("1234")
                                                                                             .build());

        userService.findByEmail(userServiceResponse.getEmail())
                   .orElseThrow(() -> new IllegalArgumentException(Error.USER_NOT_FOUND.getMessage()));


        // When
        ArticleServiceResponse response = articleService.favoriteArticle(authUser, article.getSlug());

        // Then
        assertThat(response).extracting("favorited", "favoritesCount")
                            .contains(true, 1);
    }

    @Test
    @DisplayName("즐겨찾기한 아티클을 취소한다.")
    @Transactional
    void unfavoriteArticle() throws Exception {
        // Given
        // -- 아티클 생성
        ArticleServiceResponse article = articleService.createArticle(authUser, ArticleCreateServiceRequest.builder()
                                                                                                           .title("아티클 제목")
                                                                                                           .description("설명")
                                                                                                           .body("내용")
                                                                                                           .tagList(List.of("tag1", "tag2"))
                                                                                                           .build());

        UserServiceResponse userServiceResponse = userService.signUp(UserSignUpServiceRequest.builder()
                                                                                             .email("emily@email.com")
                                                                                             .username("emily")
                                                                                             .password("1234")
                                                                                             .build());

        User user = userService.findByEmail(userServiceResponse.getEmail())
                               .orElseThrow(() -> new IllegalArgumentException(Error.USER_NOT_FOUND.getMessage()));

        articleService.favoriteArticle(authUser, article.getSlug());

        // When
        ArticleServiceResponse response = articleService.unfavoriteArticle(authUser, article.getSlug());

        // Then
        assertThat(response).extracting("favorited", "favoritesCount")
                            .contains(false, 0);
    }

    @Test
    @DisplayName("하나의 아티클을 조회한다.")
    @Transactional
    void getArticle() throws Exception {
        // Given
        // -- 아티클 생성
        ArticleServiceResponse article = articleService.createArticle(authUser, ArticleCreateServiceRequest.builder()
                                                                                                           .title("아티클 제목")
                                                                                                           .description("설명")
                                                                                                           .body("내용")
                                                                                                           .tagList(List.of("tag1", "tag2"))
                                                                                                           .build());

        AuthUser anonymous = AuthUser.builder()
                                     .anonymous(true)
                                     .build();

        // When
        ArticleServiceResponse response = articleService.getArticle(anonymous, article.getSlug());

        // Then
        assertThat(response).extracting("title", "description", "body", "tagList")
                            .contains("아티클 제목", "설명", "내용", response.getTagList());
    }

    @DisplayName("아티클을 조회한다.")
    @MethodSource
    @ParameterizedTest
    @Transactional
    void getArticles(ArticleGetArticlesServiceRequest request, int count) throws Exception {
        // Given
        //-- 즐겨찾기할 회원 생성
        userService.signUp(UserSignUpServiceRequest.builder()
                                                   .email("emily@email.com")
                                                   .username("emily")
                                                   .password("1234")
                                                   .build());

        User byEmail = userService.findByEmail("emily@email.com").get();
        AuthUser favoritedUser = AuthUser.builder()
                                         .id(byEmail.getId())
                                         .email(byEmail.getEmail())
                                         .username(byEmail.getUsername())
                                         .build();

        // -- 아티클 생성
        ArticleServiceResponse article1 = articleService.createArticle(authUser, ArticleCreateServiceRequest.builder()
                                                                                                            .title("제목")
                                                                                                            .description("설명")
                                                                                                            .body("내용")
                                                                                                            .tagList(List.of("tag1", "tag2"))
                                                                                                            .build());

        articleService.createArticle(authUser, ArticleCreateServiceRequest.builder()
                                                                          .title("제목2")
                                                                          .description("설명2")
                                                                          .body("내용2")
                                                                          .tagList(List.of("tag1", "tag3"))
                                                                          .build());

        // -- 즐겨 찾기
        articleService.favoriteArticle(favoritedUser, article1.getSlug());

        // When
        List<ArticleServiceResponse> response = articleService.getArticles(authUser, request);

        // Then
        assertThat(response.size()).isEqualTo(count);
    }

    static Stream<Arguments> getArticles() {
        return Stream.of(
                arguments(ArticleGetArticlesServiceRequest.builder()
                                                          .tag("tag1")
                                                          .pageable(PageRequest.of(0, 10))
                                                          .build(), 2),
                arguments(ArticleGetArticlesServiceRequest.builder()
                                                          .tag("tag3")
                                                          .pageable(PageRequest.of(0, 10))
                                                          .build(), 1),
                arguments(ArticleGetArticlesServiceRequest.builder()
                                                          .author("hoon")
                                                          .pageable(PageRequest.of(0, 10))
                                                          .build(), 2),
                arguments(ArticleGetArticlesServiceRequest.builder()
                                                          .favorited("emily")
                                                          .pageable(PageRequest.of(0, 10))
                                                          .build(), 1)
        );
    }

    @Test
    @DisplayName("팔로우한 회원의 아티클을 조회한다.")
    @Transactional
    void getFeedArticles() throws Exception {
        // Given
        //-- 팔로우할 회원 생성
        userService.signUp(UserSignUpServiceRequest.builder()
                                                   .email("emily@email.com")
                                                   .username("emily")
                                                   .password("1234")
                                                   .build());

        User byEmail = userService.findByEmail("emily@email.com").get();
        AuthUser followingUser = AuthUser.builder()
                                         .id(byEmail.getId())
                                         .email(byEmail.getEmail())
                                         .username(byEmail.getUsername())
                                         .build();

        // -- 팔로우
        profileService.follow(followingUser.getId(), authUser.getUsername());

        // -- 아티클 생성
        articleService.createArticle(authUser, ArticleCreateServiceRequest.builder()
                                                                          .title("제목")
                                                                          .description("설명")
                                                                          .body("내용")
                                                                          .tagList(List.of("tag1", "tag2"))
                                                                          .build());

        articleService.createArticle(authUser, ArticleCreateServiceRequest.builder()
                                                                          .title("제목2")
                                                                          .description("설명2")
                                                                          .body("내용2")
                                                                          .tagList(List.of("tag2", "tag3"))
                                                                          .build());

        articleService.createArticle(authUser, ArticleCreateServiceRequest.builder()
                                                                          .title("제목3")
                                                                          .description("설명3")
                                                                          .body("내용3")
                                                                          .tagList(List.of("tag1", "tag3"))
                                                                          .build());

        // When
        List<ArticleServiceResponse> response = articleService.getFeedArticles(followingUser, PageRequest.of(0, 10));

        // Then
        assertThat(response.size()).isEqualTo(3);
        assertThat(response).extracting("title")
                            .contains("제목", "제목2", "제목3");
        assertThat(response.get(0)).extracting("title").isEqualTo("제목3");
        assertThat(response.get(1)).extracting("title").isEqualTo("제목2");
        assertThat(response.get(2)).extracting("title").isEqualTo("제목");
    }
}