package io.hoon.realworld.api.service.profile.response;

import com.fasterxml.jackson.annotation.JsonRootName;
import io.hoon.realworld.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonRootName("profile")
public class ProfileServiceResponse {

    String username;
    String bio;
    String image;
    boolean following;

    @Builder
    private ProfileServiceResponse(String username, String bio, String image, boolean following) {
        this.username = username;
        this.bio = bio;
        this.image = image;
        this.following = following;
    }

    public static ProfileServiceResponse of(User user, boolean isFollow) {
        return ProfileServiceResponse.builder()
                                     .username(user.getUsername())
                                     .bio(user.getBio())
                                     .image(user.getImage())
                                     .following(isFollow)
                                     .build();
    }
}
