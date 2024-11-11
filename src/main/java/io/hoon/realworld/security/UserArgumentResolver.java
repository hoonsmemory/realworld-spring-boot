package io.hoon.realworld.security;

import io.hoon.realworld.domain.user.User;
import io.hoon.realworld.domain.user.UserRepository;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    private final UserRepository userRepository;

    // 컨트롤러 메서드의 인자가 User 타입인지 확인
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType() == User.class;
    }

    // 컨트롤러 메서드의 인자에 주입할 User 객체를 반환
    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
        // 현재 SecurityContext에서 인증 정보 가져오기
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        // 인증 정보가 익명 사용자일 경우 익명 사용자 객체 반환
        if (authentication instanceof AnonymousAuthenticationToken) {
            return User.anonymous();
        }

        // JWT 토큰에서 사용자 ID와 토큰 값 추출
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        String userId = jwtAuthenticationToken.getName().strip();
        String token = jwtAuthenticationToken.getToken().getTokenValue().strip();

        // 사용자 ID로 데이터베이스에서 사용자 조회 후 토큰 소유 여부 확인
        return userRepository
                .findById(Long.parseLong(userId))
                .map(user -> user.possessToken(token))
                .orElseThrow(() -> new InvalidBearerTokenException("`%s` is invalid token".formatted(token)));
    }
}