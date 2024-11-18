package io.hoon.realworld.api.service.profile;

import io.hoon.realworld.IntegrationTestSupport;
import io.hoon.realworld.api.service.profile.response.ProfileServiceResponse;
import io.hoon.realworld.api.service.user.UserService;
import io.hoon.realworld.api.service.user.request.UserLoginServiceRequest;
import io.hoon.realworld.api.service.user.request.UserSignUpServiceRequest;
import io.hoon.realworld.domain.user.User;
import io.hoon.realworld.domain.user.UserRepository;
import io.hoon.realworld.domain.user.follow.FollowRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileServiceTest extends IntegrationTestSupport {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private User hoon = null;
    private User emily = null;

    @BeforeEach
    void setUp() {
        //-- 유저1 회원가입
        userService.signUp(UserSignUpServiceRequest.builder()
                                                   .email("hoon@email.com")
                                                   .username("hoon")
                                                   .password("1234")
                                                   .build());

        //-- 유저2 회원가입
        userService.signUp(UserSignUpServiceRequest.builder()
                                                   .email("emily@email.com")
                                                   .username("emily")
                                                   .password("1234")
                                                   .build());

        //-- 유저2 로그인
        // - 로그인
        userService.login(UserLoginServiceRequest.builder()
                                                 .email("emily@email.com")
                                                 .password("1234")
                                                 .build());

        //-- id 조회
        hoon = userService.findByEmail("hoon@email.com").get();
        emily = userService.findByEmail("emily@email.com").get();
    }

    @AfterEach
    void tearDown() {
        followRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("다른 회원의 프로필을 조회한다.")
    @Transactional
    void getFollowProfile() throws Exception {
        // When
        ProfileServiceResponse response = profileService.getFollow(emily.getId(), "hoon");

        // Then
        assertThat(response)
                .extracting("username", "bio", "following")
                .contains("hoon", null, false);
    }

    @Test
    @DisplayName("hoon 이라는 이름을 가진 회원을 팔로우한다.")
    @Transactional
    void follow() throws Exception {
        // When
        ProfileServiceResponse response = profileService.follow(emily.getId(), hoon.getUsername());

        // Then
        assertThat(response)
                .extracting("username", "bio", "following")
                .contains("hoon", null, true);
    }

    @Test
    @DisplayName("hoon 이라는 이름을 가진 회원을 언팔로우한다.")
    @Transactional
    void unfollow() throws Exception {
        // Given
        profileService.follow(emily.getId(), "hoon");

        // When
        ProfileServiceResponse response = profileService.unfollow(emily.getId(), hoon.getUsername());

        // Then
        assertThat(response)
                .extracting("username", "bio", "following")
                .contains("hoon", null, false);
    }


}