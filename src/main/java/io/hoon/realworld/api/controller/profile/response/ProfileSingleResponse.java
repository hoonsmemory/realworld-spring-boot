package io.hoon.realworld.api.controller.profile.response;

import io.hoon.realworld.api.service.profile.response.ProfileServiceResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProfileSingleResponse {
    private final ProfileServiceResponse user;
}
