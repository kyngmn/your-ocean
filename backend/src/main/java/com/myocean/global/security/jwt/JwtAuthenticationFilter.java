package com.myocean.global.security.jwt;

import com.myocean.domain.auth.service.AuthService;
import com.myocean.global.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = extractTokenFromCookie(request);
            log.info("🍪 Cookie에서 추출한 토큰: {}", token != null ? "있음" : "없음");

            // 쿠키에서 토큰을 찾지 못하면 Authorization 헤더에서 찾기
            if (token == null) {
                token = extractTokenFromHeader(request);
                log.info("📋 헤더에서 추출한 토큰: {}", token != null ? "있음" : "없음");
            }

            if (token != null) {
                // 블랙리스트 확인 (로그아웃된 토큰)
                if (authService.isBlacklisted(token)) {
                    log.warn("🚫 블랙리스트에 등록된 토큰 (로그아웃됨)");
                    filterChain.doFilter(request, response);
                    return;
                }

                // 토큰 유효성 검증
                if (jwtUtil.validateToken(token)) {
                    Integer userId = jwtUtil.getUserIdFromToken(token);
                    log.info("👤 JWT에서 추출한 사용자 ID: {}", userId);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(userId.toString());
                    log.info("🔐 UserDetails 로드 완료: {}", userDetails.getUsername());

                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("✅ SecurityContext에 인증 정보 설정 완료");
                } else {
                    log.warn("❌ 토큰이 유효하지 않음");
                }
            } else {
                log.warn("❌ 토큰이 없음");
            }
        } catch (Exception e) {
            log.error("JWT 인증 실패: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.info("🔍 Authorization 헤더: {}", bearerToken);

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            log.info("✂️ Bearer에서 추출한 토큰: {}...", token.substring(0, Math.min(token.length(), 20)));
            return token;
        }

        // Swagger cookieAuth는 특별한 헤더로 전송됨
        String cookieHeader = request.getHeader("Cookie");
        if (cookieHeader != null && cookieHeader.contains("accessToken=")) {
            String[] cookies = cookieHeader.split(";");
            for (String cookie : cookies) {
                cookie = cookie.trim();
                if (cookie.startsWith("accessToken=")) {
                    return cookie.substring("accessToken=".length());
                }
            }
        }
        return null;
    }
}