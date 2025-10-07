package com.myocean.global.util;

import com.myocean.global.security.userdetails.CustomUserDetails;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final JwtUtil jwtUtil;

    public Integer getCurrentUserId(HttpServletRequest request) {
        String token = extractTokenFromCookie(request);
        if (token != null && jwtUtil.validateToken(token)) {
            return jwtUtil.getUserIdFromToken(token);
        }
        // JWT가 없거나 유효하지 않으면 인증 오류 발생
        throw new GeneralException(ErrorStatus.UNAUTHORIZED);
    }

    public Integer getCurrentUserId() {
        // Spring Security Context에서 사용자 ID 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
            && !"anonymousUser".equals(authentication.getPrincipal())) {

            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                return userDetails.getUserId();
            }
        }
        // 인증되지 않은 경우 인증 오류 발생
        throw new GeneralException(ErrorStatus.UNAUTHORIZED);
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
}