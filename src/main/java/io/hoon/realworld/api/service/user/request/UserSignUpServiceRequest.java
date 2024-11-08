package io.hoon.realworld.api.service.user.request;

import io.hoon.realworld.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignUpServiceRequest {

    private String email;
    private String username;
    private String password;

    @Builder
    private UserSignUpServiceRequest(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public User toEntity() {
        return User.create(email, username, password);
    }
}
