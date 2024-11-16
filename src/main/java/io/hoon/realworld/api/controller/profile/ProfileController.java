package io.hoon.realworld.api.controller.profile;

import io.hoon.realworld.api.controller.profile.response.ProfileSingleResponse;
import io.hoon.realworld.api.service.profile.ProfileService;
import io.hoon.realworld.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/api/profiles/{username}")
    public ProfileSingleResponse get(AuthUser user, @PathVariable String username) {
        return new ProfileSingleResponse(profileService.getFollow(user.getId(), username));
    }

    @PostMapping("/api/profiles/{username}/follow")
    public ProfileSingleResponse follow(AuthUser user, @PathVariable String username) {
        return new ProfileSingleResponse(profileService.follow(user.getId(), username));
    }

    @DeleteMapping("/api/profiles/{username}/follow")
    public ProfileSingleResponse unfollow(AuthUser user, @PathVariable String username) {
        return new ProfileSingleResponse(profileService.unfollow(user.getId(), username));
    }
}
