package io.hoon.realworld.domain.user;

import io.hoon.realworld.IntegrationTestSupport;
import io.hoon.realworld.exception.Error;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest extends IntegrationTestSupport {

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("이메일로 회원을 조회한다.")
    @Transactional
    void findByEmail() throws Exception {
        // Given
        String username = "hoon";
        String email = "hoon@email.com";
        String password = "password";

        User user = User.create(email, username, password);
        userRepository.save(user);

        // When
        User byEmail = userRepository.findByEmail(email).orElseGet(() -> {
            throw new IllegalArgumentException(Error.USER_NOT_FOUND.getMessage());
        });

        // Then
    	assertEquals(user.getEmail(), byEmail.getEmail());
    }
}