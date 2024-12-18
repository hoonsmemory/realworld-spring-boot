package io.hoon.realworld.api.service.user;

import io.hoon.realworld.IntegrationTestSupport;
import io.hoon.realworld.api.service.user.request.UserLoginServiceRequest;
import io.hoon.realworld.api.service.user.request.UserSignUpServiceRequest;
import io.hoon.realworld.api.service.user.request.UserUpdateServiceRequest;
import io.hoon.realworld.api.service.user.response.UserServiceResponse;
import io.hoon.realworld.domain.user.User;
import io.hoon.realworld.domain.user.UserRepository;
import io.hoon.realworld.exception.Error;
import io.hoon.realworld.security.AuthUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private static final String username = "hoon";
    private static final String email = "hoon@email.com";
    private static final String password = "password";

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("신규 화원을 등록한다.")
    void singUp() throws Exception {
        // Given
        UserSignUpServiceRequest request = UserSignUpServiceRequest.builder()
                                                                   .username(username)
                                                                   .email(email)
                                                                   .password(password)
                                                                   .build();

        // When
        UserServiceResponse response = userService.signUp(request);

        // Then
        User byEmail = userRepository.findByEmail(email)
                                     .orElseThrow(() -> new IllegalArgumentException(Error.USER_NOT_FOUND.getMessage()));

        assertThat(response)
                .extracting("email", "username")
                .contains(email, username);

        assertThat(byEmail.getPassword()).isNotEqualTo(password);
    }

    @Test
    @DisplayName("로그인한다.")
    void login() throws Exception {
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

        // When
        UserServiceResponse response = userService.login(loginRequest);

        // Then
        assertThat(response)
                .extracting("email", "username")
                .contains(email, username);

        assertThat(response.getToken()).isNotNull()
                                       .matches("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]*$");
    }

    @Test
    @DisplayName("회원 정보를 수정한다.")
    @Transactional
    void update() throws Exception {
        // Given
        // - 회원가입
        UserSignUpServiceRequest signUpRequest = UserSignUpServiceRequest.builder()
                                                                   .username(username)
                                                                   .email(email)
                                                                   .password(password)
                                                                   .build();

        userService.signUp(signUpRequest);
        Optional<User> user = userRepository.findByEmail(signUpRequest.getEmail());
        AuthUser authUser = AuthUser.builder()
                                    .id(user.get().getId())
                                    .email(user.get().getEmail())
                                    .username(user.get().getUsername())
                                    .password(user.get().getPassword())
                                    .build();

        // - 회원 정보 수정
        String email = "update@email.com";
        String username = "updateHoon";
        String password = "";
        String bio = "test";

        UserUpdateServiceRequest updateRequest = UserUpdateServiceRequest.builder()
                                                                   .email(Optional.of(email))
                                                                   .username(Optional.of(username))
                                                                   .password(Optional.of(password))
                                                                   .image(Optional.empty())
                                                                   .bio(Optional.of(bio))
                                                                   .build();

        // When
        UserServiceResponse response = userService.update(authUser, updateRequest);

        // Then
        assertThat(response)
                .extracting("email", "username", "bio")
                .contains(email, username, bio);
    }

}