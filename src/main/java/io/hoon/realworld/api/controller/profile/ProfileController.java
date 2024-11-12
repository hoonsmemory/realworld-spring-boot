package io.hoon.realworld.api.controller.profile;

import io.hoon.realworld.api.service.profile.ProfileService;
import io.hoon.realworld.api.service.profile.response.ProfileSingleResponse;
import io.hoon.realworld.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/api/profiles/{username}")
    public ProfileSingleResponse get(User user, @PathVariable String username) {
        return profileService.get(user.getId(), username);
    }

    @PostMapping("/api/profiles/{username}/follow")
    public ProfileSingleResponse follow(User user, @PathVariable String username) {
        return profileService.follow(user.getId(), username);
    }

    @DeleteMapping("/api/profiles/{username}/follow")
    public ProfileSingleResponse unfollow(User user, @PathVariable String username) {
        return profileService.unfollow(user.getId(), username);
    }
}
