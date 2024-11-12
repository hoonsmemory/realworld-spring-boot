package io.hoon.realworld.domain.user;

import io.hoon.realworld.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Entity
public class User extends BaseEntity {

    @GeneratedValue(strategy = IDENTITY)
    @Id
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(length = 50, nullable = false)
    private String username;

    @Column(length = 200, nullable = false)
    private String password;

    private String bio;

    private String image;

    @Transient
    private String token;

    @Transient
    private boolean anonymous;

    @Builder
    private User(Long id, String email, String username, String password, String bio, String image, String token, boolean anonymous) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.bio = bio;
        this.image = image;
        this.token = token;
        this.anonymous = anonymous;
    }

    public static User create(String email, String username, String password) {
        return User.builder()
                   .email(email)
                   .username(username)
                   .password(password)
                   .build();
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public static User anonymous() {
        return User.builder().anonymous(true).build();
    }

    public boolean isAnonymous() {
        return this.id == null && this.anonymous;
    }

    public User possessToken(String token) {
        this.token = token;
        return this;
    }


    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updatePassword(String password, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    public void updateBio(String bio) {
        this.bio = bio;
    }

    public void updateImage(String image) {
        this.image = image;
    }
}


