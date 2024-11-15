package io.hoon.realworld.api.controller.user.response;

import io.hoon.realworld.api.service.user.response.UserServiceResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserSingleResponse {
    private final UserServiceResponse user;
}
