package io.hoon.realworld.api.service.profile;

import io.hoon.realworld.api.service.profile.response.ProfileSingleResponse;
import io.hoon.realworld.api.service.user.UserService;
import io.hoon.realworld.domain.user.follow.Follow;
import io.hoon.realworld.domain.user.follow.FollowRepository;
import io.hoon.realworld.domain.user.User;
import io.hoon.realworld.exception.Error;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProfileService {

    private final UserService userService;
    private final FollowRepository followRepository;

    public ProfileSingleResponse get(long myId, String targetUsername) {
        User target = userService.findByUsername(targetUsername)
                                 .orElseThrow(() -> new NoSuchElementException(targetUsername));

        boolean isFollow = followRepository.findByFollowerIdAndFolloweeId(myId, target.getId())
                                           .isPresent();

        return ProfileSingleResponse.of(target, isFollow);
    }

    @Transactional
    public ProfileSingleResponse follow(long myId, String targetUsername) {
        User target = userService.findByUsername(targetUsername)
                                 .orElseThrow(() ->
                                         new NoSuchElementException(targetUsername)
                                 );

        followRepository.findByFollowerIdAndFolloweeId(myId, target.getId())
                        .ifPresent(follow -> {
                            throw new IllegalStateException(Error.ALREADY_FOLLOWING_USER.getMessage());
                        });

        Follow newFollow = Follow.builder()
                                 .follower(User.builder()
                                               .id(myId)
                                               .build())
                                 .followee(target)
                                 .build();

        followRepository.save(newFollow);

        return ProfileSingleResponse.of(target, true);
    }

    @Transactional
    public ProfileSingleResponse unfollow(long myId, String targetUsername) {
        User target = userService.findByUsername(targetUsername)
                                 .orElseThrow(() ->
                                         new NoSuchElementException(targetUsername)
                                 );

        Follow follow = followRepository.findByFollowerIdAndFolloweeId(myId, target.getId())
                                        .orElseThrow(() -> new NoSuchElementException(targetUsername));


        followRepository.delete(follow);

        return ProfileSingleResponse.of(target, false);
    }
}
