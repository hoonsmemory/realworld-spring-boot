package io.hoon.realworld.domain.user;

import io.hoon.realworld.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest extends IntegrationTestSupport {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("이메일로 회원을 조회한다.")
    @Transactional
    void findByEmail() throws Exception {
        // Given
        String userName = "hoon";
        String email = "hoon@email.com";
        String password = "password";

        User user = User.create(email, userName, password);
        userRepository.save(user);

        // When
        User byEmail = userRepository.findByEmail(email);

        // Then
    	assertEquals(user.getEmail(), byEmail.getEmail());
    }
}