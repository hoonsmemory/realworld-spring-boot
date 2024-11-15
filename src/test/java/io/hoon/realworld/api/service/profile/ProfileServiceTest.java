package io.hoon.realworld.api.service.profile;

import io.hoon.realworld.IntegrationTestSupport;
import io.hoon.realworld.api.service.profile.response.ProfileSingleResponse;
import io.hoon.realworld.api.service.user.UserService;
import io.hoon.realworld.api.service.user.request.UserSignUpServiceRequest;
import io.hoon.realworld.api.service.user.response.UserServiceResponse;
import io.hoon.realworld.domain.user.follow.FollowRepository;
import io.hoon.realworld.domain.user.User;
import io.hoon.realworld.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    private static long myId = 0l;

    @BeforeEach
    void setUp() {
        //-- 유저1 회원가입
        UserSignUpServiceRequest userSignUpServiceRequest1 = UserSignUpServiceRequest.builder()
                                                                                     .email("hoon@email.com")
                                                                                     .username("hoon")
                                                                                     .password("1234")
                                                                                     .build();

        userService.signUp(userSignUpServiceRequest1);

        //-- 유저2 회원가입
        UserSignUpServiceRequest userSignUpServiceRequest2 = UserSignUpServiceRequest.builder()
                                                                                     .email("emily@email.com")
                                                                                     .username("emily")
                                                                                     .password("1234")
                                                                                     .build();

        UserServiceResponse userServiceResponse = userService.signUp(userSignUpServiceRequest2);

        //-- id 조회
        Optional<User> emily = userService.findByEmail(userServiceResponse.getEmail());
        myId = emily.get().getId();
    }

    @AfterEach
    void tearDown() {
        followRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("다른 회원의 프로필을 조회한다.")
    @Transactional
    void getProfile() throws Exception {
        // When
        ProfileSingleResponse response = profileService.get(myId, "hoon");

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
        ProfileSingleResponse response = profileService.follow(myId, "hoon");

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
        profileService.follow(myId, "hoon");

        // When
        ProfileSingleResponse response = profileService.unfollow(myId, "hoon");

        // Then
        assertThat(response)
                .extracting("username", "bio", "following")
                .contains("hoon", null, false);
    }


}