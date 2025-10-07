package com.myocean.domain.auth.controller;

import com.myocean.domain.auth.dto.request.CallbackRequest;
import com.myocean.domain.auth.dto.request.JoinRequest;
import com.myocean.domain.auth.dto.request.ReissueRequest;
import com.myocean.domain.auth.dto.response.LoginUrlResponse;
import com.myocean.domain.auth.dto.response.TokenResponse;
import com.myocean.domain.auth.service.AuthService;
import com.myocean.global.util.JwtUtil;
import com.myocean.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Operation(summary = "Google 로그인", description = "Google 로그인 페이지 리디렉션 url을 반환합니다.")
    @GetMapping("/google/login")
    public ApiResponse<LoginUrlResponse> googleLogin() {
        LoginUrlResponse response = authService.getGoogleLoginUrl();
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "Google 로그인 콜백", description = "Google에서 받은 authorization code로 JWT를 발급하고 쿠키에 저장합니다.")
    @PostMapping("/google/callback")
    public ApiResponse<TokenResponse> googleCallback(@Valid @RequestBody CallbackRequest request, HttpServletResponse httpResponse) {
        TokenResponse response = authService.processGoogleCallback(request);

        // JWT 토큰을 HttpOnly 쿠키로 설정
        setAccessTokenCookie(httpResponse, response.accessToken());
        setRefreshTokenCookie(httpResponse, response.refreshToken());

        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "Google 소셜 회원가입", description = "Google 계정으로 회원가입을 처리하고 쿠키에 저장합니다.")
    @PostMapping("/google/join")
    public ApiResponse<TokenResponse> googleJoin(@Valid @RequestBody JoinRequest request, HttpServletResponse httpResponse) {
        TokenResponse response = authService.processGoogleJoin(request);

        // JWT 토큰을 HttpOnly 쿠키로 설정
        setAccessTokenCookie(httpResponse, response.accessToken());
        setRefreshTokenCookie(httpResponse, response.refreshToken());

        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "토큰 재발급", description = "accessToken이 만료되었을 때, refreshToken으로 다시 발급합니다.")
    @PostMapping("/google/reissue")
    public ApiResponse<TokenResponse> reissueToken(@Valid @RequestBody ReissueRequest request, HttpServletResponse httpResponse) {
        TokenResponse response = authService.reissueToken(request);

        // 새로 발급된 Access Token을 쿠키에도 설정
        setAccessTokenCookie(httpResponse, response.accessToken());

        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "Google OAuth 자동 처리", description = "Google OAuth 후 자동으로 로그인/회원가입을 판단하여 처리합니다.")
    @PostMapping("/google/oauth")
    public ApiResponse<TokenResponse> googleOAuth(@Valid @RequestBody CallbackRequest request, HttpServletResponse httpResponse) {
        TokenResponse response = authService.processGoogleOAuth(request);

        // JWT 토큰을 HttpOnly 쿠키로 설정
        setAccessTokenCookie(httpResponse, response.accessToken());
        setRefreshTokenCookie(httpResponse, response.refreshToken());

        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "로그아웃", description = "로그아웃시 accessToken을 무효화하고 쿠키를 삭제합니다.")
    @PostMapping("/google/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String token, HttpServletResponse httpResponse) {
        // Bearer 토큰에서 실제 토큰 추출
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        // Redis에서 refresh token 삭제 및 access token 블랙리스트 추가
        authService.logout(actualToken);

        // Access Token 쿠키 삭제
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);
        httpResponse.addCookie(accessTokenCookie);

        // Refresh Token 쿠키 삭제
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);
        httpResponse.addCookie(refreshTokenCookie);

        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "[개발용] 테스트 로그인", description = "개발/테스트 목적으로 특정 사용자 ID로 JWT 토큰을 발급합니다.")
    @PostMapping("/test/login/{userId}")
    public ApiResponse<TokenResponse> testLogin(@PathVariable Integer userId, HttpServletResponse httpResponse) {
        // 개발용 임시 토큰 생성
        String testEmail = "test" + userId + "@test.com";
        String accessToken = jwtUtil.generateToken(userId, testEmail);

        TokenResponse response = new TokenResponse(accessToken, null);

        // JWT 토큰을 HttpOnly 쿠키로 설정
        setAccessTokenCookie(httpResponse, accessToken);

        return ApiResponse.onSuccess(response);
    }

    private void setAccessTokenCookie(HttpServletResponse response, String token) {
        Cookie accessTokenCookie = new Cookie("accessToken", token);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(86400); // 24시간

        // 환경별 Secure 설정
        boolean isProduction = !frontendUrl.contains("localhost");
        accessTokenCookie.setSecure(isProduction);

        response.addCookie(accessTokenCookie);
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String token) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", token);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(14 * 24 * 60 * 60); // 14일

        // 환경별 Secure 설정
        boolean isProduction = !frontendUrl.contains("localhost");
        refreshTokenCookie.setSecure(isProduction);

        response.addCookie(refreshTokenCookie);
    }
}