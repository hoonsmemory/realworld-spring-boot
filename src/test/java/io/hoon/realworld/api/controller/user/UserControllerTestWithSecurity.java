package io.hoon.realworld.api.controller.user;

import io.hoon.realworld.IntegrationTestSupport;
import io.hoon.realworld.api.service.user.UserService;
import io.hoon.realworld.api.service.user.request.UserLoginServiceRequest;
import io.hoon.realworld.api.service.user.request.UserSignUpServiceRequest;
import io.hoon.realworld.api.service.user.response.UserSingleResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTestWithSecurity extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private static final String email = "hoon@email.com";
    private static final String userName = "hoon";
    private static final String password = "password";


    @Test
    @DisplayName("회원정보를 가져온다.")
    @Transactional
    void getUser() throws Exception {
        // Given
        // - 회원가입
        UserSignUpServiceRequest signUpRequest = UserSignUpServiceRequest.builder()
                                                                         .username(userName)
                                                                         .email(email)
                                                                         .password(password)
                                                                         .build();

        userService.signUp(signUpRequest);

        // - 로그인
        UserLoginServiceRequest loginRequest = UserLoginServiceRequest.builder()
                                                                      .email(email)
                                                                      .password(password)
                                                                      .build();

        UserSingleResponse response = userService.login(loginRequest);

        // When // Then
        mockMvc.perform(get("/api/user")
                       .header("Authorization", "Token " + response.getToken())
               ).andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.user.email").value(email))
               .andExpect(jsonPath("$.user.token").value(notNullValue()))
               .andExpect(jsonPath("$.user.username").value(userName))
               .andExpect(jsonPath("$.user.bio").value(nullValue()))
               .andExpect(jsonPath("$.user.image").value(nullValue()));
    }
}
