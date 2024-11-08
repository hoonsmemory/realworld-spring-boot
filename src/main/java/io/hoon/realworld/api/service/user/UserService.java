package io.hoon.realworld.api.service.user;

import io.hoon.realworld.api.service.user.request.UserSignUpServiceRequest;
import io.hoon.realworld.api.service.user.response.UserSingleResponse;
import io.hoon.realworld.domain.user.User;
import io.hoon.realworld.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public UserSingleResponse signUp(UserSignUpServiceRequest request) {
        User user = request.toEntity();
        user.encodePassword(passwordEncoder);
        User savedUser = userRepository.save(user);
        return UserSingleResponse.of(savedUser);
    }

}
