package io.hoon.realworld.domain.follow;

import io.hoon.realworld.domain.BaseEntity;
import io.hoon.realworld.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "follows")
@Entity
public class Follow extends BaseEntity {

    @GeneratedValue(strategy = IDENTITY)
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "followee")
    private User followee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "follower")
    private User follower;

    @Builder
    private Follow(Long id, User followee, User follower) {
        this.id = id;
        this.followee = followee;
        this.follower = follower;
    }

    public Follow create(User followee, User follower) {
        return Follow.builder()
                     .followee(followee)
                     .follower(follower)
                     .build();
    }
}