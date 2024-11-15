package io.hoon.realworld.api.controller.user;

import io.hoon.realworld.ControllerTestSupport;
import io.hoon.realworld.api.controller.user.request.UserLoginRequest;
import io.hoon.realworld.api.controller.user.request.UserSignUpRequest;
import io.hoon.realworld.api.service.user.response.UserServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends ControllerTestSupport {

    private static final String email = "hoon@email.com";
    private static final String username = "hoon";
    private static final String password = "password";

    @DisplayName("신규 회원을 등록한다.")
    @Test
    void signUp() throws Exception {
        // given
        UserSignUpRequest request = UserSignUpRequest.builder()
                                                     .email(email)
                                                     .username(username)
                                                     .password(password)
                                                     .build();

        UserServiceResponse userServiceResponse = UserServiceResponse.builder()
                                                                     .email(email)
                                                                     .username(username)
                                                                     .build();

        when(userService.signUp(any())).thenReturn(userServiceResponse);

        // when // then
        mockMvc.perform(
                       post("/api/users")
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(objectMapper.writeValueAsString(Map.of("user", request)))
               )
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.user.email").value(email))
               .andExpect(jsonPath("$.user.token").value(nullValue()))
               .andExpect(jsonPath("$.user.username").value(username))
               .andExpect(jsonPath("$.user.bio").value(nullValue()))
               .andExpect(jsonPath("$.user.image").value(nullValue()));
    }

    @DisplayName("회원 가입 시 이메일은 필수다.")
    @Test
    void signUpWithoutEmail() throws Exception {
        // given
        UserSignUpRequest request = UserSignUpRequest.builder()
                                                     .username(username)
                                                     .password(password)
                                                     .build();

        // when // then
        mockMvc.perform(
                       post("/api/users")
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(objectMapper.writeValueAsString(Map.of("user", request)))
               )
               .andDo(print())
               .andExpect(status().isBadRequest());
    }

    @DisplayName("회원 가입 시 이름은 필수다.")
    @Test
    void signUpWithoutUsername() throws Exception {
        // given
        UserSignUpRequest request = UserSignUpRequest.builder()
                                                     .email(email)
                                                     .password(password)
                                                     .build();

        // when // then
        mockMvc.perform(
                       post("/api/users")
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(objectMapper.writeValueAsString(request))
               )
               .andDo(print())
               .andExpect(status().isBadRequest());
    }

    @DisplayName("회원 가입 시 비밀번호는 필수다.")
    @Test
    void signUpWithoutPassword() throws Exception {
        // given
        UserSignUpRequest request = UserSignUpRequest.builder()
                                                     .email(email)
                                                     .username(username)
                                                     .build();

        // when // then
        mockMvc.perform(
                       post("/api/users")
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(objectMapper.writeValueAsString(Map.of("user", request)))
               )
               .andDo(print())
               .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인한다.")
    void login() throws Exception {
        // Given
        UserLoginRequest request = UserLoginRequest.builder()
                                                   .email(email)
                                                   .password(password)
                                                   .build();

        when(userService.login(any())).thenReturn(UserServiceResponse.builder()
                                                                     .email(email)
                                                                     .username(username)
                                                                     .token("eyJ")
                                                                     .build());

        // When // Then
        mockMvc.perform(
                       post("/api/users/login")
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(objectMapper.writeValueAsString(Map.of("user", request)))
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
    @DisplayName("로그인 시 이메일은 필수다.")
    void loginWithoutEmail() throws Exception {
        // Given
        UserLoginRequest request = UserLoginRequest.builder()
                                                   .password(password)
                                                   .build();

        // When // Then
        mockMvc.perform(
                       post("/api/users/login")
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(objectMapper.writeValueAsString(Map.of("user", request)))
               )
               .andDo(print())
               .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 시 비밀번호는 필수다.")
    void loginWithoutPassword() throws Exception {
        // Given
        UserLoginRequest request = UserLoginRequest.builder()
                                                   .email(email)
                                                   .build();

        // When // Then
        mockMvc.perform(
                       post("/api/users/login")
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(objectMapper.writeValueAsString(Map.of("user", request)))
               )
               .andDo(print())
               .andExpect(status().isBadRequest());
    }

}