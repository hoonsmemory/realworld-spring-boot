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
    private String userName;

    @Column(length = 200, nullable = false)
    private String password;

    private String bio;

    private String image;

    @Builder
    private User(String email, String userName, String password, String bio, String image) {
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.bio = bio;
        this.image = image;
    }

    public static User create(String email, String userName, String password) {
        return User.builder()
                   .email(email)
                   .userName(userName)
                   .password(password)
                   .build();

    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }
}


