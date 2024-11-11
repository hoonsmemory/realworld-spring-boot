package io.hoon.realworld.security;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.hoon.realworld.exception.Error;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.BearerTokenErrors;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CustomPrefixBearerTokenResolver implements BearerTokenResolver {
    // 정규 표현식을 사용하여 "Token "으로 시작하는 토큰을 추출
    private static final Pattern AUTHORIZATION_PATTERN =
            Pattern.compile("^Token (?<token>[a-zA-Z0-9-._~+/]+=*)$", Pattern.CASE_INSENSITIVE);

    @Override
    public String resolve(HttpServletRequest request) {
        // Authorization 헤더에서 토큰을 추출
        String authorizationHeaderToken = resolveFromAuthorizationHeader(request);
        // 요청 파라미터에서 토큰을 추출 (지원되는 경우)
        String parameterToken =
                isParameterTokenSupportedForRequest(request) ? resolveFromRequestParameters(request) : null;

        // Authorization 헤더와 요청 파라미터에서 모두 토큰이 발견될 경우 예외 발생
        if (authorizationHeaderToken != null) {
            if (parameterToken != null) {
                BearerTokenError error =
                        BearerTokenErrors.invalidRequest(Error.MULTIPLE_TOKENS_FOUND.getMessage());
                throw new OAuth2AuthenticationException(error);
            }
            return authorizationHeaderToken;
        }
        return null;
    }

    private String resolveFromAuthorizationHeader(HttpServletRequest request) {
        // Authorization 헤더에서 토큰을 추출
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.startsWithIgnoreCase(authorization, "token")) return null;
        Matcher matcher = AUTHORIZATION_PATTERN.matcher(authorization);
        if (!matcher.matches()) {
            BearerTokenError error = BearerTokenErrors.invalidToken("Bearer token is malformed");
            throw new OAuth2AuthenticationException(error);
        }
        return matcher.group("token");
    }

    private boolean isParameterTokenSupportedForRequest(HttpServletRequest request) {
        // 요청 메서드가 POST이고 Content-Type이 application/x-www-form-urlencoded이거나 GET인 경우 true 반환
        return (("POST".equals(request.getMethod())
                && MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(request.getContentType()))
                || "GET".equals(request.getMethod()));
    }

    private String resolveFromRequestParameters(HttpServletRequest request) {
        // 요청 파라미터에서 access_token 값을 추출
        String[] values = request.getParameterValues("access_token");
        if (values == null || values.length == 0) return null;
        if (values.length == 1) return values[0];
        BearerTokenError error = BearerTokenErrors.invalidRequest("Found multiple bearer tokens in the request.");
        throw new OAuth2AuthenticationException(error);
    }
}