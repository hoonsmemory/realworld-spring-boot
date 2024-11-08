package io.hoon.realworld.api.service.user.response;

import com.fasterxml.jackson.annotation.JsonRootName;
import io.hoon.realworld.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonRootName("user")
public class UserSingleResponse {

    private String email;
    private String username;
    private String password;
    private String bio;
    private String image;

    @Builder
    private UserSingleResponse(String email, String username, String password, String bio, String image) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.bio = bio;
        this.image = image;
    }

    public static UserSingleResponse of(User user) {
        return UserSingleResponse.builder()
                                 .email(user.getEmail())
                                 .username(user.getUserName())
                                 .password(user.getPassword())
                                 .bio(user.getBio())
                                 .image(user.getImage())
                                 .build();
    }

}
