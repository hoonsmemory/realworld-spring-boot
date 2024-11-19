package io.hoon.realworld.api.service.user;

import io.hoon.realworld.api.service.user.request.UserLoginServiceRequest;
import io.hoon.realworld.api.service.user.request.UserSignUpServiceRequest;
import io.hoon.realworld.api.service.user.request.UserUpdateServiceRequest;
import io.hoon.realworld.api.service.user.response.UserServiceResponse;
import io.hoon.realworld.domain.user.User;
import io.hoon.realworld.domain.user.UserRepository;
import io.hoon.realworld.exception.Error;
import io.hoon.realworld.security.AuthUser;
import io.hoon.realworld.security.BearerTokenSupplier;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final BearerTokenSupplier bearerTokenSupplier;

    @Transactional
    public UserServiceResponse signUp(UserSignUpServiceRequest request) {
        User user = request.toEntity();
        user.encodePassword(passwordEncoder);

        User savedUser = userRepository.save(user);
        return UserServiceResponse.of(savedUser);
    }

    public UserServiceResponse login(UserLoginServiceRequest request) {
        return userRepository.findByEmail(request.getEmail())
                             .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                             .map(user -> {
                                 String token = bearerTokenSupplier.supply(user);
                                 user.possessToken(token);
                                 return UserServiceResponse.of(user);
                             })
                             .orElseThrow(() -> new IllegalArgumentException(Error.INVALID_EMAIL_OR_PASSWORD.getMessage()));
    }

    @Transactional
    public UserServiceResponse update(AuthUser user, UserUpdateServiceRequest request) {
        User userEntity = user.toEntity();

        request.getEmail()
               .ifPresent(email -> {

                   // 이메일 중복 체크
                   userRepository.findByEmail(email).ifPresent(existUser -> {
                       if (!existUser.getId().equals(user.getId())) {
                           throw new IllegalArgumentException(Error.EMAIL_ALREADY_EXIST.getMessage());
                       }
                   });

                   userEntity.updateEmail(email);
               });

        request.getUsername()
               .ifPresent(userEntity::updateUsername);

        request.getPassword()
               .ifPresent(password -> userEntity.updatePassword(password, passwordEncoder));

        request.getImage()
               .ifPresent(userEntity::updateImage);

        request.getBio()
               .ifPresent(userEntity::updateBio);

        return UserServiceResponse.of(userEntity);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }
}
