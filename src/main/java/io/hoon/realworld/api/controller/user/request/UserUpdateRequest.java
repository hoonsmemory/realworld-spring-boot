package io.hoon.realworld.api.controller.user.request;

import com.fasterxml.jackson.annotation.JsonRootName;
import io.hoon.realworld.api.service.user.request.UserUpdateServiceRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@NoArgsConstructor
@JsonRootName(value = "user")
public class UserUpdateRequest {

    private String email;
    private String username;
    private String password;
    private String image;
    private String bio;

    @Builder
    public UserUpdateRequest(String email, String username, String password, String image, String bio) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.image = image;
        this.bio = bio;
    }

    public UserUpdateServiceRequest toServiceRequest() {
        return UserUpdateServiceRequest.builder()
                .email(Optional.ofNullable(email))
                .username(Optional.ofNullable(username))
                .password(Optional.ofNullable(password))
                .image(Optional.ofNullable(image))
                .bio(Optional.ofNullable(bio))
                .build();
    }
}
