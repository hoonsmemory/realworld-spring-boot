package io.hoon.realworld.security;

import io.hoon.realworld.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthUser {
    private final long id;
    private final String email;
    private final String token;
    private final String username;
    private final String bio;
    private final String image;
    private final String password;
    private final boolean anonymous;

    @Builder
    private AuthUser(long id, String email, String token, String username, String bio, String image, String password, boolean anonymous) {
        this.id = id;
        this.email = email;
        this.token = token;
        this.username = username;
        this.bio = bio;
        this.image = image;
        this.password = password;
        this.anonymous = anonymous;
    }

    public User toEntity() {
        return User.builder()
                   .id(id)
                   .email(email)
                   .token(token)
                   .username(username)
                   .bio(bio)
                   .image(image)
                   .build();
    }
}
