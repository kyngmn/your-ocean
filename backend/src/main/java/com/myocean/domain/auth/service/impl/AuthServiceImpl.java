package com.myocean.domain.auth.service.impl;

import com.myocean.domain.auth.dto.request.CallbackRequest;
import com.myocean.domain.auth.dto.request.JoinRequest;
import com.myocean.domain.auth.dto.request.ReissueRequest;
import com.myocean.domain.auth.dto.response.LoginUrlResponse;
import com.myocean.domain.auth.dto.response.TokenResponse;
import com.myocean.domain.auth.service.AuthService;
import com.myocean.domain.user.entity.User;
import com.myocean.domain.user.enums.Provider;
import com.myocean.domain.user.repository.UserRepository;
import com.myocean.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${OAUTH_REDIRECT_URI:http://localhost:8080/login/oauth2/code/google}")
    private String redirectUri;

    @Override
    public LoginUrlResponse getGoogleLoginUrl() {
        String googleAuthUrl = "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=" + googleClientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=openid email profile" +
                "&access_type=offline" +
                "&prompt=consent";

        log.info("Generated Google login URL: {}", googleAuthUrl);
        return new LoginUrlResponse(googleAuthUrl);
    }

    @Override
    public TokenResponse processGoogleCallback(CallbackRequest request) {
        try {
            // 1. authorization code로 Google에서 access token 가져오기
            String accessToken = getGoogleAccessToken(request.code());

            // 2. access token으로 사용자 정보 가져오기
            Map<String, Object> userInfo = getGoogleUserInfo(accessToken);

            // 3. 기존 사용자인지 확인 (회원가입된 사용자만 로그인 허용)
            String socialId = (String) userInfo.get("id");
            Optional<User> existingUser = userRepository.findByProviderAndSocialId(Provider.GOOGLE, socialId);

            if (existingUser.isEmpty()) {
                log.warn("User not found for social ID: {}. Please register first.", socialId);
                throw new RuntimeException("User not found. Please register first.");
            }

            User user = existingUser.get();

            // 4. JWT 토큰 생성
            String jwtToken = jwtUtil.generateToken(user.getId(), user.getEmail());

            log.info("Successfully processed Google callback for user: {}", user.getEmail());

            return new TokenResponse(jwtToken, "refresh_token_placeholder");

        } catch (Exception e) {
            log.error("Error processing Google callback: ", e);
            throw new RuntimeException("Failed to process Google callback", e);
        }
    }

    @Override
    public TokenResponse processGoogleJoin(JoinRequest request) {
        try {
            // 1. authorization code로 Google에서 access token 가져오기
            String accessToken = getGoogleAccessToken(request.code());

            // 2. access token으로 사용자 정보 가져오기
            Map<String, Object> userInfo = getGoogleUserInfo(accessToken);

            // 3. 이미 가입된 사용자인지 확인
            String socialId = (String) userInfo.get("id");
            Optional<User> existingUser = userRepository.findByProviderAndSocialId(Provider.GOOGLE, socialId);

            if (existingUser.isPresent()) {
                log.warn("User already exists for social ID: {}. Please login instead.", socialId);
                throw new RuntimeException("User already exists. Please login instead.");
            }

            // 4. 새 유저 생성 (닉네임 포함)
            User user = createNewUser(userInfo, request.nickname());

            // 5. JWT 토큰 생성
            String jwtToken = jwtUtil.generateToken(user.getId(), user.getEmail());

            log.info("Successfully processed Google join for user: {} with nickname: {}", user.getEmail(), request.nickname());

            return new TokenResponse(jwtToken, "refresh_token_placeholder");

        } catch (Exception e) {
            log.error("Error processing Google join: ", e);
            throw new RuntimeException("Failed to process Google join", e);
        }
    }

    @Override
    public TokenResponse processGoogleOAuth(CallbackRequest request) {
        try {
            // 1. authorization code로 Google에서 access token 가져오기
            String accessToken = getGoogleAccessToken(request.code());

            // 2. access token으로 사용자 정보 가져오기
            Map<String, Object> userInfo = getGoogleUserInfo(accessToken);

            // 3. 사용자 존재 여부 확인 후 자동 처리
            String socialId = (String) userInfo.get("id");
            String email = (String) userInfo.get("email");
            String name = (String) userInfo.get("name");

            Optional<User> existingUser = userRepository.findByProviderAndSocialId(Provider.GOOGLE, socialId);

            User user;
            if (existingUser.isPresent()) {
                // 기존 사용자 - 로그인 처리
                user = existingUser.get();
                log.info("Existing user login: {}", user.getEmail());
            } else {
                // 신규 사용자 - 자동 회원가입 (임시 닉네임 사용)
                String tempNickname = name != null ? name : "사용자" + System.currentTimeMillis();

                user = User.builder()
                        .email(email)
                        .provider(Provider.GOOGLE)
                        .socialId(socialId)
                        .nickname(tempNickname)
                        .build();

                user = userRepository.save(user);
                log.info("New user auto-registered: {} with nickname: {}", user.getEmail(), tempNickname);
            }

            // 4. JWT 토큰 생성
            String jwtToken = jwtUtil.generateToken(user.getId(), user.getEmail());

            return new TokenResponse(jwtToken, "refresh_token_placeholder");

        } catch (Exception e) {
            log.error("Error processing Google OAuth: ", e);
            throw new RuntimeException("Failed to process Google OAuth", e);
        }
    }

    @Override
    public TokenResponse reissueToken(ReissueRequest request) {
        // TODO: 토큰 재발급 로직 구현
        // 1. refresh token 검증
        // 2. 새로운 access token 생성

        log.info("Reissuing token with refresh token: {}", request.refreshToken());

        return new TokenResponse("new_access_token", "new_refresh_token");
    }

    @Override
    public void logout(String token) {
        // TODO: 로그아웃 로직 구현
        // 1. access token 무효화
        // 2. refresh token 삭제

        log.info("Processing logout for token: {}", token);
    }

    private String getGoogleAccessToken(String authorizationCode) {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("code", authorizationCode);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", redirectUri);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
        Map<String, Object> responseBody = response.getBody();

        if (responseBody != null && responseBody.containsKey("access_token")) {
            return (String) responseBody.get("access_token");
        } else {
            throw new RuntimeException("Failed to get access token from Google");
        }
    }

    private Map<String, Object> getGoogleUserInfo(String accessToken) {
        String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, Map.class);
        return response.getBody();
    }

    private User findOrCreateUser(Map<String, Object> userInfo) {
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");
        String socialId = (String) userInfo.get("id");

        Optional<User> existingUser = userRepository.findByProviderAndSocialId(Provider.GOOGLE, socialId);

        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        // 새 사용자 생성 - 임시 닉네임 설정
        String tempNickname = name != null ? name : "사용자" + System.currentTimeMillis();

        User newUser = User.builder()
                .email(email)
                .provider(Provider.GOOGLE)
                .socialId(socialId)
                .nickname(tempNickname)
                .build();

        return userRepository.save(newUser);
    }

    private User createNewUser(Map<String, Object> userInfo, String nickname) {
        String email = (String) userInfo.get("email");
        String socialId = (String) userInfo.get("id");

        User newUser = User.builder()
                .email(email)
                .provider(Provider.GOOGLE)
                .socialId(socialId)
                .nickname(nickname)
                .build();

        return userRepository.save(newUser);
    }
}