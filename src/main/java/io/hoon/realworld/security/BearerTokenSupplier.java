package io.hoon.realworld.security;

import io.hoon.realworld.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class BearerTokenSupplier {
    private final JwtEncoder jwtEncoder;

    // JWT 토큰을 생성하는 메서드
    public String supply(User user) {
        Instant now = Instant.now(); // 현재 시간
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                                             .issuer("https://realworld.hoon.io") // 발행자
                                             .issuedAt(now) // 발행 시간
                                             .expiresAt(now.plusSeconds(3600)) // 만료 시간 (3600초 후)
                                             .subject(user.getId().toString()) // 주제 (사용자 ID)
                                             .build();

        JwtEncoderParameters parameters = JwtEncoderParameters.from(claimsSet);
        String token = jwtEncoder.encode(parameters).getTokenValue(); // JWT 토큰 인코딩
        log.info("Generated bearer token with user id `{}`: {}", user.getId(), token); // 토큰 생성 로그
        return token; // 생성된 토큰 반환
    }
}
