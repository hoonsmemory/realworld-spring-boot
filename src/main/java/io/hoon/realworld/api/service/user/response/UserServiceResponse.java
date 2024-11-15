package io.hoon.realworld.api.service.user.response;

import com.fasterxml.jackson.annotation.JsonRootName;
import io.hoon.realworld.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonRootName("user")
public class UserServiceResponse {

    private String email;
    private String token;
    private String username;
    private String bio;
    private String image;

    @Builder
    private UserServiceResponse(String email, String token, String username, String bio, String image) {
        this.email = email;
        this.token = token;
        this.username = username;
        this.bio = bio;
        this.image = image;
    }

    public static UserServiceResponse of(User user) {
        return UserServiceResponse.builder()
                                  .email(user.getEmail())
                                  .token(user.getToken())
                                  .username(user.getUsername())
                                  .bio(user.getBio())
                                  .image(user.getImage())
                                  .build();
    }
}
