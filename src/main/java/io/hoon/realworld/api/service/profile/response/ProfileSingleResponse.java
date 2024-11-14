package io.hoon.realworld.api.service.profile.response;

import com.fasterxml.jackson.annotation.JsonRootName;
import io.hoon.realworld.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonRootName("profile")
public class ProfileSingleResponse {

    String username;
    String bio;
    String image;
    boolean following;

    @Builder
    private ProfileSingleResponse(String username, String bio, String image, boolean following) {
        this.username = username;
        this.bio = bio;
        this.image = image;
        this.following = following;
    }

    public static ProfileSingleResponse of(User user, boolean isFollow) {
        return ProfileSingleResponse.builder()
                                    .username(user.getUsername())
                                    .bio(user.getBio())
                                    .image(user.getImage())
                                    .following(isFollow)
                                    .build();
    }
}
