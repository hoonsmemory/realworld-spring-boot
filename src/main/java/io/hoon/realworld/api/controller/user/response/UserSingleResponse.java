package io.hoon.realworld.api.controller.user.response;

import io.hoon.realworld.api.service.user.response.UserServiceResponse;
import lombok.Getter;

@Getter
public class UserSingleResponse {
    UserServiceResponse user;

    public UserSingleResponse(UserServiceResponse user) {
        this.user = user;
    }

}
