package io.hoon.realworld.api.service.user;

import io.hoon.realworld.IntegrationTestSupport;
import io.hoon.realworld.api.service.user.request.UserLoginServiceRequest;
import io.hoon.realworld.api.service.user.request.UserSignUpServiceRequest;
import io.hoon.realworld.api.service.user.response.UserSingleResponse;
import io.hoon.realworld.domain.user.User;
import io.hoon.realworld.domain.user.UserRepository;
import io.hoon.realworld.exception.Error;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class UserServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("신규 화원을 등록한다.")
    void singUp() throws Exception {
        // Given
        String userName = "hoon";
        String email = "hoon@email.com";
        String password = "password";

        UserSignUpServiceRequest request = UserSignUpServiceRequest.builder()
                                                                   .username(userName)
                                                                   .email(email)
                                                                   .password(password)
                                                                   .build();

        // When
        UserSingleResponse response = userService.signUp(request);

        // Then
        User byEmail = userRepository.findByEmail(email)
                                     .orElseGet(() -> {
                                         throw new IllegalArgumentException(Error.USER_NOT_FOUND.getMessage());
                                     });

        assertThat(response)
                .extracting("email", "username", "password")
                .contains("hoon", "hoon@email.com", byEmail.getPassword());
    }

    @Test
    @DisplayName("로그인한다.")
    void login() throws Exception {
        // Given
        String userName = "hoon";
        String email = "hoon@email.com";
        String password = "password";

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

        // When
        UserSingleResponse response = userService.login(loginRequest);

        // Then
        assertThat(response)
                .extracting("email", "username")
                .contains(email, userName);

        assertThat(response.getToken()).isNotNull().matches("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]*$");
    }

}