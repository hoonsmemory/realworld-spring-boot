package io.hoon.realworld.api.service.user.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLoginServiceRequest {

    private String email;
    private String password;

    @Builder
    private UserLoginServiceRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
