package io.hoon.realworld.api.service.user.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@NoArgsConstructor
public class UserUpdateServiceRequest {

    private Optional<String> email;
    private Optional<String> username;
    private Optional<String> password;
    private Optional<String> image;
    private Optional<String> bio;

    @Builder
    private UserUpdateServiceRequest(Optional<String> email, Optional<String> username, Optional<String> password, Optional<String> image, Optional<String> bio) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.image = image;
        this.bio = bio;
    }
}
