package io.hoon.realworld.api.service.user;

import io.hoon.realworld.api.service.user.request.UserLoginServiceRequest;
import io.hoon.realworld.api.service.user.request.UserSignUpServiceRequest;
import io.hoon.realworld.api.service.user.response.UserSingleResponse;
import io.hoon.realworld.security.BearerTokenSupplier;
import io.hoon.realworld.domain.user.User;
import io.hoon.realworld.domain.user.UserRepository;
import io.hoon.realworld.exception.Error;
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
    private final BearerTokenSupplier bearerTokenSupplier;

    @Transactional
    public UserSingleResponse signUp(UserSignUpServiceRequest request) {
        User user = request.toEntity();
        user.encodePassword(passwordEncoder);

        User savedUser = userRepository.save(user);
        return UserSingleResponse.of(savedUser);
    }

    public UserSingleResponse login(UserLoginServiceRequest request) {
        return userRepository.findByEmail(request.getEmail())
                             .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                             .map(user -> {
                                 String token = bearerTokenSupplier.supply(user);
                                 user.possessToken(token);
                                 return UserSingleResponse.of(user);
                             })
                             .orElseThrow(() -> new IllegalArgumentException(Error.INVALID_EMAIL_OR_PASSWORD.getMessage()));
    }

}
