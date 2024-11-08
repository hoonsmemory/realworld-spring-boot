package io.hoon.realworld.api.controller.user;

import io.hoon.realworld.api.controller.user.request.UserSignUpRequest;
import io.hoon.realworld.api.service.user.UserService;
import io.hoon.realworld.api.service.user.response.UserSingleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
}
