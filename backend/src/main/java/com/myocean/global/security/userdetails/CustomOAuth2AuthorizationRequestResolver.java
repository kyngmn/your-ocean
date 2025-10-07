package com.myocean.global.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final DefaultOAuth2AuthorizationRequestResolver defaultResolver;

    public CustomOAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository, "/oauth2/authorization");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request);
        return customizeAuthorizationRequest(request, authorizationRequest);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request, clientRegistrationId);
        return customizeAuthorizationRequest(request, authorizationRequest);
    }

    private OAuth2AuthorizationRequest customizeAuthorizationRequest(
            HttpServletRequest request, OAuth2AuthorizationRequest authorizationRequest) {

        if (authorizationRequest == null) {
            return null;
        }

        log.info("=== OAuth2 로그인 시작 ===");
        log.info("🚀 요청 URL: {}", request.getRequestURL().toString());
        log.info("🚀 요청 Host: {}", request.getHeader("Host"));
        log.info("🚀 X-Forwarded-Proto: {}", request.getHeader("X-Forwarded-Proto"));
        log.info("🚀 X-Forwarded-Host: {}", request.getHeader("X-Forwarded-Host"));
        log.info("🚀 User-Agent: {}", request.getHeader("User-Agent"));
        log.info("🚀 Referer: {}", request.getHeader("Referer"));

        log.info("🔗 OAuth2 Authorization Request 정보:");
        log.info("🔗 Client ID: {}", authorizationRequest.getClientId());
        log.info("🔗 Redirect URI: {}", authorizationRequest.getRedirectUri());
        log.info("🔗 State: {}", authorizationRequest.getState());
        log.info("🔗 Authorization URI: {}", authorizationRequest.getAuthorizationUri());
        log.info("🔗 Scopes: {}", authorizationRequest.getScopes());

        return authorizationRequest;
    }
}