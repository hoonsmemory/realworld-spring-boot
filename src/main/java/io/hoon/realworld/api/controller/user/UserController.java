package io.hoon.realworld.api.controller.user;

import io.hoon.realworld.api.controller.user.request.UserLoginRequest;
import io.hoon.realworld.api.controller.user.request.UserSignUpRequest;
import io.hoon.realworld.api.service.user.UserService;
import io.hoon.realworld.api.service.user.response.UserSingleResponse;
import io.hoon.realworld.domain.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/api/users")
    public UserSingleResponse signUp(@Valid @RequestBody UserSignUpRequest request) {
        return userService.signUp(request.toServiceRequest());
    }

    @PostMapping("/api/users/login")
    public UserSingleResponse login(@Valid @RequestBody UserLoginRequest request) {
        return userService.login(request.toServiceRequest());
    }

    @GetMapping("/api/user")
    public UserSingleResponse get(User user) {
        return UserSingleResponse.of(user);
    }

}
