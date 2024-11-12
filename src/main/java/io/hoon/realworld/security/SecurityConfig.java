package io.hoon.realworld.security;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

import io.hoon.realworld.exception.ExceptionHandleFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ExceptionHandleFilter exceptionHandleFilter) throws Exception {
        return http.httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 인증 비활성화
                   .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
                   .formLogin(AbstractHttpConfigurer::disable) // 폼 로그인 비활성화
                   .cors(withDefaults()) // CORS 설정 활성화
                   .authorizeHttpRequests( // HTTP 요청에 대한 권한 설정
                           requests -> requests.requestMatchers(
                                                       HttpMethod.POST,
                                                       "/api/users",
                                                       "/api/users/login")
                                               .permitAll() // 특정 POST 요청에 대해 모든 회원에게 접근 허용
                                               .requestMatchers(
                                                       HttpMethod.GET,
                                                       "/api/articles/{slug}/comments",
                                                       "/api/articles/{slug}",
                                                       "/api/articles",
                                                       "/api/profiles/{username}",
                                                       "/api/tags")
                                               .permitAll() // 특정 GET 요청에 대해 모든 회원에게 접근 허용
                                               .anyRequest()
                                               .authenticated()) // 그 외의 모든 요청은 인증 필요
                   .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults())) // OAuth2 리소스 서버 설정
                   .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS)) // 세션 관리 정책을 무상태로 설정
                   .exceptionHandling( // 예외 처리 설정
                           handler -> handler.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                                             .accessDeniedHandler(new BearerTokenAccessDeniedHandler()))
                   .addFilterBefore(exceptionHandleFilter, UsernamePasswordAuthenticationFilter.class) // 커스텀 필터 추가
                   .build(); // SecurityFilterChain 빌드
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOriginPatterns(List.of("*"));
        cors.setAllowedMethods(List.of("*"));
        cors.setAllowedHeaders(List.of("*"));
        cors.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtDecoder jwtDecoder(@Value("${security.key.public}") RSAPublicKey rsaPublicKey) {
        return NimbusJwtDecoder.withPublicKey(rsaPublicKey)
                               .build();
    }

    @Bean
    public JwtEncoder jwtEncoder(
            @Value("${security.key.public}") RSAPublicKey rsaPublicKey,
            @Value("${security.key.private}") RSAPrivateKey rsaPrivateKey) {
        JWK jwk = new RSAKey.Builder(rsaPublicKey).privateKey(rsaPrivateKey)
                                                  .build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }
}