package io.hoon.realworld.domain.user.follow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollowerIdAndFolloweeId(long followerId, Long followeeId);

    Optional<List<Follow>> findByFollowerId(long followerId);
}
