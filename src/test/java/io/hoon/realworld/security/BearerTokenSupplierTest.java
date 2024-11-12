package io.hoon.realworld.security;

import io.hoon.realworld.IntegrationTestSupport;
import io.hoon.realworld.domain.user.User;
import io.hoon.realworld.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BearerTokenSupplierTest extends IntegrationTestSupport {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BearerTokenSupplier bearerTokenSupplier;

    @Test
    @DisplayName("회원 정보를 이용하여 JWT 토큰을 생성한다.")
    void Supply() {
        // Given
        // -- 회원가입
        String username = "hoon";
        String email = "hoon@email.com";
        String password = "password";
        User user = User.create(email, username, password);
        userRepository.save(user);

        // -- 회원 조회
        Optional<User> byEmail = userRepository.findByEmail(email);

        // When
        String token = bearerTokenSupplier.supply(byEmail.get());

        // Then
        assertNotNull(token);
        assertTrue(token.startsWith("eyJ"));
    }

}