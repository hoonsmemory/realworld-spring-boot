package io.hoon.realworld.api.controller.user;

import io.hoon.realworld.IntegrationTestSupport;
import io.hoon.realworld.api.controller.user.request.UserUpdateRequest;
import io.hoon.realworld.api.service.user.UserService;
import io.hoon.realworld.api.service.user.request.UserLoginServiceRequest;
import io.hoon.realworld.api.service.user.request.UserSignUpServiceRequest;
import io.hoon.realworld.api.service.user.response.UserServiceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTestWithSecurity extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private static final String email = "hoon@email.com";
    private static final String username = "hoon";
    private static final String password = "password";
    private UserServiceResponse response = null;

    @BeforeEach
    void setUp() {
        // Given
        // - 회원가입
        UserSignUpServiceRequest signUpRequest = UserSignUpServiceRequest.builder()
                                                                         .username(username)
                                                                         .email(email)
                                                                         .password(password)
                                                                         .build();

        userService.signUp(signUpRequest);

        // - 로그인
        UserLoginServiceRequest loginRequest = UserLoginServiceRequest.builder()
                                                                      .email(email)
                                                                      .password(password)
                                                                      .build();

        response = userService.login(loginRequest);
    }

    @Test
    @DisplayName("회원정보를 가져온다.")
    @Transactional
    void getUser() throws Exception {
        // When // Then
        mockMvc.perform(get("/api/user")
                       .header("Authorization", "Token " + response.getToken())
               )
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.user.email").value(email))
               .andExpect(jsonPath("$.user.token").value(notNullValue()))
               .andExpect(jsonPath("$.user.username").value(username))
               .andExpect(jsonPath("$.user.bio").value(nullValue()))
               .andExpect(jsonPath("$.user.image").value(nullValue()));
    }

    @Test
    @DisplayName("회원 정보를 수정한다.")
    @Transactional
    void updateUser() throws Exception {
        // Given
        UserUpdateRequest request = UserUpdateRequest.builder()
                                                     .email("updateHoon@email.com")
                                                     .username("updateHoon")
                                                     .bio("test")
                                                     .build();

        // When // Then
        mockMvc.perform(put("/api/user")
                       .header("Authorization", "Token " + response.getToken())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request))
               )
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.user.email").value("updateHoon@email.com"))
               .andExpect(jsonPath("$.user.token").value(notNullValue()))
               .andExpect(jsonPath("$.user.username").value("updateHoon"))
               .andExpect(jsonPath("$.user.bio").value("test"))
               .andExpect(jsonPath("$.user.image").value(nullValue()));
    }
}
