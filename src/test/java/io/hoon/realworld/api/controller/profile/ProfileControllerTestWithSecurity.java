package io.hoon.realworld.api.controller.profile;

import io.hoon.realworld.IntegrationTestSupport;
import io.hoon.realworld.api.service.profile.ProfileService;
import io.hoon.realworld.api.service.user.UserService;
import io.hoon.realworld.api.service.user.request.UserLoginServiceRequest;
import io.hoon.realworld.api.service.user.request.UserSignUpServiceRequest;
import io.hoon.realworld.api.service.user.response.UserServiceResponse;
import io.hoon.realworld.domain.user.User;
import io.hoon.realworld.domain.user.UserRepository;
import io.hoon.realworld.domain.user.follow.FollowRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
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

    private UserServiceResponse loginResponse = null;
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
        loginResponse = userService.login(UserLoginServiceRequest.builder()
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
    void getProfile() throws Exception {
        // When  // Then
        mockMvc.perform(get("/api/profiles/hoon")
                       .header("Authorization", "Token " + loginResponse.getToken()))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.profile.username").value("hoon"))
               .andExpect(jsonPath("$.profile.following").value(false));
    }

    @Test
    @DisplayName("다른 회원을 팔로우한다.")
    void follow() throws Exception {
        // When // Then
        mockMvc.perform(post("/api/profiles/{username}/follow", hoon.getUsername())
                       .header("Authorization", "Token " + loginResponse.getToken()))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.profile.username").value("hoon"))
               .andExpect(jsonPath("$.profile.following").value(true));
    }

    @Test
    @DisplayName("다른 회원을 언팔로우한다.")
    @Transactional
    void unfollow() throws Exception {
        // Given
        profileService.follow(emily.getId(), hoon.getUsername());

        // When // Then
        assertThat(profileService.getFollow(emily.getId(), hoon.getUsername()).isFollowing()).isTrue();

        mockMvc.perform(delete("/api/profiles/{username}/follow", hoon.getUsername())
                       .header("Authorization", "Token " + loginResponse.getToken()))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.profile.username").value("hoon"))
               .andExpect(jsonPath("$.profile.following").value(false));
    }
}