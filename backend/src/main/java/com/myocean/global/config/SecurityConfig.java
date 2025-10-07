package com.myocean.global.config;

import com.myocean.global.auth.JwtAuthenticationFilter;
import com.myocean.global.auth.OAuth2AuthenticationSuccessHandler;
import com.myocean.global.auth.CustomOAuth2AuthorizationRequestResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    private final CustomOAuth2AuthorizationRequestResolver customOAuth2AuthorizationRequestResolver;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**", "/v3/api-docs").permitAll()
                .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                .requestMatchers("/login.html", "/login").permitAll()
                .requestMatchers("/api/v1/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(authorizationEndpointConfig ->
                    authorizationEndpointConfig.authorizationRequestResolver(customOAuth2AuthorizationRequestResolver))
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler((request, response, exception) -> {
                    log.error("=== OAuth2 로그인 실패 ===");
                    log.error("🔴 실패 원인: {}", exception.getMessage());
                    log.error("🔴 예외 타입: {}", exception.getClass().getSimpleName());
                    log.error("🔴 요청 URL: {}", request.getRequestURL().toString());
                    log.error("🔴 Query String: {}", request.getQueryString());
                    log.error("🔴 요청 Host: {}", request.getHeader("Host"));
                    log.error("🔴 X-Forwarded-Proto: {}", request.getHeader("X-Forwarded-Proto"));
                    log.error("🔴 X-Forwarded-Host: {}", request.getHeader("X-Forwarded-Host"));
                    log.error("🔴 User-Agent: {}", request.getHeader("User-Agent"));
                    log.error("🔴 Referer: {}", request.getHeader("Referer"));

                    // 쿠키 정보 로그
                    Cookie[] cookies = request.getCookies();
                    if (cookies != null) {
                        for (Cookie cookie : cookies) {
                            log.error("🍪 쿠키: {} = {}", cookie.getName(), cookie.getValue());
                        }
                    } else {
                        log.error("🍪 쿠키 없음");
                    }

                    String frontendUrl = System.getenv("FRONTEND_URL");
                    log.error("🔴 환경변수 FRONTEND_URL: {}", frontendUrl);
                    if (frontendUrl == null || frontendUrl.contains("localhost")) {
                        frontendUrl = "http://localhost:3000";
                    }
                    String redirectUrl = frontendUrl + "/login?error=oauth_failed";
                    log.error("🔴 리다이렉트 URL: {}", redirectUrl);
                    response.sendRedirect(redirectUrl);
                })
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}