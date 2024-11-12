package io.hoon.realworld.domain.follow;

import io.hoon.realworld.IntegrationTestSupport;
import io.hoon.realworld.domain.user.User;
import io.hoon.realworld.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FollowRepositoryTest extends IntegrationTestSupport {

    @Autowired
    FollowRepository followRepository;

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void tearDown() {
        followRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("팔로우한 회원을 조회한다.")
    @Transactional
    void findByFollowerIdAndFolloweeId() {
        // Given
        User follower = User.builder()
                            .username("hoon")
                            .password("1234")
                            .email("hoon@email.com")
                            .bio("test bio")
                            .image("test image path")
                            .build();

        User followee = User.builder()
                            .username("emily")
                            .password("1234")
                            .email("emily@email.com")
                            .bio("test bio")
                            .image("test image path")
                            .build();

        userRepository.saveAll(List.of(followee, follower));

        Follow newFollow = Follow.builder()
                                 .follower(follower)
                                 .followee(followee)
                                 .build();

        followRepository.save(newFollow);

        // When
        Follow follow = followRepository.findByFollowerIdAndFolloweeId(follower.getId(), followee.getId()).get();

        // Then
        assertThat(follow.getFollower())
                .extracting("email", "username")
                .contains("hoon@email.com", "hoon");
        assertThat(follow.getFollowee())
                .extracting("email", "username")
                .contains("emily@email.com", "emily");

    }
}