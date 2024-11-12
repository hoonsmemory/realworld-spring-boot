package io.hoon.realworld.api.controller.profile;

import io.hoon.realworld.IntegrationTestSupport;
import io.hoon.realworld.api.service.profile.ProfileService;
import io.hoon.realworld.api.service.profile.response.ProfileSingleResponse;
import io.hoon.realworld.api.service.user.UserService;
import io.hoon.realworld.api.service.user.request.UserLoginServiceRequest;
import io.hoon.realworld.api.service.user.request.UserSignUpServiceRequest;
import io.hoon.realworld.api.service.user.response.UserSingleResponse;
import io.hoon.realworld.domain.follow.FollowRepository;
import io.hoon.realworld.domain.user.User;
import io.hoon.realworld.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProfileControllerTestWithSecurity extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private long myId = 0l;

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

        //-- 유저2 회원가입
        UserSignUpServiceRequest userSignUpServiceRequest2 = UserSignUpServiceRequest.builder()
                                                                                     .email("emily@email.com")
                                                                                     .username("emily")
                                                                                     .password("1234")
                                                                                     .build();

        UserSingleResponse userSingleResponse = userService.signUp(userSignUpServiceRequest2);

        //-- 유저2 로그인
        // - 로그인
        UserLoginServiceRequest loginRequest = UserLoginServiceRequest.builder()
                                                                      .email("emily@email.com")
                                                                      .password("1234")
                                                                      .build();

        response = userService.login(loginRequest);

        //-- id 조회
        Optional<User> emily = userService.findByEmail(userSingleResponse.getEmail());
        myId = emily.get().getId();
    }

    @AfterEach
    void tearDown() {
        followRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("다른 회원의 프로필을 조회한다.")
    void getProfile() throws Exception {
        // When  // Then
        mockMvc.perform(get("/api/profiles/hoon")
                       .header("Authorization", "Token " + response.getToken()))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.profile.username").value("hoon"))
               .andExpect(jsonPath("$.profile.following").value(false));
    }

    @Test
    @DisplayName("다른 회원을 팔로우한다.")
    void follow() throws Exception {
        // When // Then
        mockMvc.perform(post("/api/profiles/hoon/follow")
                       .header("Authorization", "Token " + response.getToken()))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.profile.username").value("hoon"))
               .andExpect(jsonPath("$.profile.following").value(true));
    }

    @Test
    @DisplayName("다른 회원을 언팔로우한다.")
    void unfollow() throws Exception {
        // Given
        ProfileSingleResponse profileSingleResponse = profileService.follow(myId, "hoon");

        // When // Then
        assertThat(profileSingleResponse.isFollowing()).isTrue();

        mockMvc.perform(delete("/api/profiles/hoon/follow")
                       .header("Authorization", "Token " + response.getToken()))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.profile.username").value("hoon"))
               .andExpect(jsonPath("$.profile.following").value(false));
    }
}