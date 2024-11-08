package io.hoon.realworld.api.controller.user;

import io.hoon.realworld.ControllerTestSupport;
import io.hoon.realworld.api.controller.user.request.UserSignUpRequest;
import io.hoon.realworld.api.service.user.response.UserSingleResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends ControllerTestSupport {

    @DisplayName("신규 회원을 등록한다.")
    @Test
    void signUp() throws Exception {
        // given
        String email = "hoon@email.com";
        String userName = "hoon";
        String password = "password";
        UserSignUpRequest request = UserSignUpRequest.builder()
                                                     .email(email)
                                                     .username(userName)
                                                     .password(password)
                                                     .build();

        UserSingleResponse userSingleResponse = UserSingleResponse.builder()
                                                                  .email(email)
                                                                  .username(userName)
                                                                  .password(password)
                                                                  .build();

        when(userService.signUp(any())).thenReturn(userSingleResponse);

        // when // then
        mockMvc.perform(
                       post("/api/users")
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(objectMapper.writeValueAsString(request))
               )
               .andDo(print())
               .andExpect(status().isOk());
    }

}