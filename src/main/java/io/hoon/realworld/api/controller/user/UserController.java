package io.hoon.realworld.api.controller.user;

import io.hoon.realworld.api.controller.user.request.UserLoginRequest;
import io.hoon.realworld.api.controller.user.request.UserSignUpRequest;
import io.hoon.realworld.api.controller.user.request.UserUpdateRequest;
import io.hoon.realworld.api.controller.user.response.UserSingleResponse;
import io.hoon.realworld.api.service.user.UserService;
import io.hoon.realworld.api.service.user.response.UserServiceResponse;
import io.hoon.realworld.security.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/api/users")
    public UserSingleResponse signUp(@Valid @RequestBody UserSignUpRequest request) {
        return new UserSingleResponse(userService.signUp(request.toServiceRequest()));
    }

    @PostMapping("/api/users/login")
    public UserSingleResponse login(@Valid @RequestBody UserLoginRequest request) {
        return new UserSingleResponse(userService.login(request.toServiceRequest()));
    }

    @GetMapping("/api/user")
    public UserSingleResponse get(AuthUser user) {
        return new UserSingleResponse(UserServiceResponse.of(user.toEntity()));
    }

    @PutMapping("/api/user")
    public UserSingleResponse update(AuthUser user, @RequestBody UserUpdateRequest request) {
        return new UserSingleResponse(userService.update(user, request.toServiceRequest()));
    }

}
