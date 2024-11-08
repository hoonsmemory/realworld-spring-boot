package io.hoon.realworld.api.service.user;

import io.hoon.realworld.IntegrationTestSupport;
import io.hoon.realworld.api.service.user.request.UserSignUpServiceRequest;
import io.hoon.realworld.api.service.user.response.UserSingleResponse;
import io.hoon.realworld.domain.user.User;
import io.hoon.realworld.domain.user.UserRepository;
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
    void test() throws Exception {
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
        User byEmail = userRepository.findByEmail(email);
        assertThat(response)
                .extracting("email", "username", "password")
                .contains("hoon", "hoon@email.com", byEmail.getPassword());


    }

}