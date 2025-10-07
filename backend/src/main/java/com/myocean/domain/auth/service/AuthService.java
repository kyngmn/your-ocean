package com.myocean.domain.auth.service;

import com.myocean.domain.auth.dto.request.CallbackRequest;
import com.myocean.domain.auth.dto.request.JoinRequest;
import com.myocean.domain.auth.dto.request.ReissueRequest;
import com.myocean.domain.auth.dto.response.LoginUrlResponse;
import com.myocean.domain.auth.dto.response.TokenResponse;
import com.myocean.domain.user.entity.User;
import com.myocean.domain.user.enums.Provider;
import com.myocean.domain.user.repository.UserRepository;
import com.myocean.global.util.JwtUtil;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${OAUTH_REDIRECT_URI:http://localhost:8080/login/oauth2/code/google}")
    private String redirectUri;

    public LoginUrlResponse getGoogleLoginUrl() {
        String googleAuthUrl = "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=" + googleClientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=openid email profile" +
                "&access_type=offline" +
                "&prompt=consent";

        log.info("Google 로그인 URL 생성 완료");
        return new LoginUrlResponse(googleAuthUrl);
    }

    public TokenResponse processGoogleCallback(CallbackRequest request) {
        try {
            // 1. authorization code로 Google에서 access token 가져오기
            String googleAccessToken = getGoogleAccessToken(request.code());

            // 2. access token으로 사용자 정보 가져오기
            Map<String, Object> userInfo = getGoogleUserInfo(googleAccessToken);

            // 3. 기존 사용자인지 확인 (회원가입된 사용자만 로그인 허용)
            String socialId = (String) userInfo.get("id");
            Optional<User> existingUser = userRepository.findByProviderAndSocialId(Provider.GOOGLE, socialId);

            if (existingUser.isEmpty()) {
                log.warn("미등록 사용자 로그인 시도 - socialId: {}", socialId);
                throw new GeneralException(ErrorStatus.USER_NOT_REGISTERED_BY_GOOGLE);
            }

            User user = existingUser.get();

            // 4. JWT 토큰 생성
            String jwtAccessToken = jwtUtil.generateToken(user.getId(), user.getEmail());
            String jwtRefreshToken = jwtUtil.generateRefreshToken(user.getId());

            // 5. Refresh Token을 Redis에 저장
            saveRefreshToken(user.getId(), jwtRefreshToken);

            log.info("Google 로그인 완료 - userId: {}, email: {}", user.getId(), user.getEmail());
            return new TokenResponse(jwtAccessToken, jwtRefreshToken);

        } catch (GeneralException e) {
            throw e; // GeneralException은 그대로 전달
        } catch (Exception e) {
            log.error("Google 로그인 처리 실패", e);
            throw new GeneralException(ErrorStatus.GOOGLE_LOGIN_FAILED);
        }
    }

    public TokenResponse processGoogleJoin(JoinRequest request) {
        try {
            // 1. authorization code로 Google에서 access token 가져오기
            String googleAccessToken = getGoogleAccessToken(request.code());

            // 2. access token으로 사용자 정보 가져오기
            Map<String, Object> userInfo = getGoogleUserInfo(googleAccessToken);

            // 3. 이미 가입된 사용자인지 확인
            String socialId = (String) userInfo.get("id");
            Optional<User> existingUser = userRepository.findByProviderAndSocialId(Provider.GOOGLE, socialId);

            if (existingUser.isPresent()) {
                log.warn("이미 가입된 사용자 - socialId: {}", socialId);
                throw new GeneralException(ErrorStatus.USER_ALREADY_REGISTERED);
            }

            // 4. 새 유저 생성 (닉네임 포함)
            User user = createNewUser(userInfo, request.nickname());

            // 5. JWT 토큰 생성
            String jwtAccessToken = jwtUtil.generateToken(user.getId(), user.getEmail());
            String jwtRefreshToken = jwtUtil.generateRefreshToken(user.getId());

            // 6. Refresh Token을 Redis에 저장
            saveRefreshToken(user.getId(), jwtRefreshToken);

            log.info("Google 회원가입 완료 - userId: {}, email: {}, nickname: {}", user.getId(), user.getEmail(), request.nickname());

            return new TokenResponse(jwtAccessToken, jwtRefreshToken);

        } catch (GeneralException e) {
            throw e; // GeneralException은 그대로 전달
        } catch (Exception e) {
            log.error("Google 회원가입 처리 실패", e);
            throw new GeneralException(ErrorStatus.GOOGLE_JOIN_FAILED);
        }
    }

    public TokenResponse processGoogleOAuth(CallbackRequest request) {
        try {
            // 1. authorization code로 Google에서 access token 가져오기
            String googleAccessToken = getGoogleAccessToken(request.code());

            // 2. access token으로 사용자 정보 가져오기
            Map<String, Object> userInfo = getGoogleUserInfo(googleAccessToken);

            // 3. 사용자 존재 여부 확인 후 자동 처리
            String socialId = (String) userInfo.get("id");
            String email = (String) userInfo.get("email");
            String name = (String) userInfo.get("name");

            Optional<User> existingUser = userRepository.findByProviderAndSocialId(Provider.GOOGLE, socialId);

            User user;
            if (existingUser.isPresent()) {
                // 기존 사용자 - 로그인 처리
                user = existingUser.get();
                log.info("기존 사용자 로그인 - userId: {}, email: {}", user.getId(), user.getEmail());
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
                log.info("신규 사용자 자동 회원가입 - userId: {}, email: {}, nickname: {}", user.getId(), user.getEmail(), tempNickname);
            }

            // 4. JWT 토큰 생성
            String jwtAccessToken = jwtUtil.generateToken(user.getId(), user.getEmail());
            String jwtRefreshToken = jwtUtil.generateRefreshToken(user.getId());

            // 5. Refresh Token을 Redis에 저장
            saveRefreshToken(user.getId(), jwtRefreshToken);

            return new TokenResponse(jwtAccessToken, jwtRefreshToken);

        } catch (GeneralException e) {
            throw e; // GeneralException은 그대로 전달
        } catch (Exception e) {
            log.error("Google OAuth 처리 실패", e);
            throw new GeneralException(ErrorStatus.GOOGLE_OAUTH_FAILED);
        }
    }

    public TokenResponse reissueToken(ReissueRequest request) {
        String refreshToken = request.refreshToken();

        // 1. Refresh Token 유효성 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            log.warn("유효하지 않은 Refresh Token");
            throw new GeneralException(ErrorStatus.INVALID_REFRESHTOKEN);
        }

        // 2. Refresh Token에서 userId 추출
        Integer userId = jwtUtil.getUserIdFromToken(refreshToken);

        // 3. Redis에 저장된 Refresh Token과 일치하는지 확인
        String storedRefreshToken = getRefreshToken(userId);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            log.warn("Redis에 저장된 Refresh Token과 불일치 - userId: {}", userId);
            throw new GeneralException(ErrorStatus.INVALID_REFRESHTOKEN);
        }

        // 4. User 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 5. 새로운 Access Token 생성
        String newAccessToken = jwtUtil.generateToken(user.getId(), user.getEmail());

        log.info("토큰 재발급 완료 - userId: {}", userId);

        // Refresh Token은 그대로 사용 (재발급하지 않음)
        return new TokenResponse(newAccessToken, refreshToken);
    }

    public void logout(String token) {
        try {
            // 1. Access Token에서 userId 추출
            Integer userId = jwtUtil.getUserIdFromToken(token);

            // 2. Refresh Token 삭제 (Redis)
            deleteRefreshToken(userId);

            // 3. Access Token 블랙리스트 추가 (만료 시간까지만 보관)
            addToBlacklist(token);

            log.info("로그아웃 완료 - userId: {}", userId);

        } catch (Exception e) {
            log.error("로그아웃 처리 실패", e);
            throw new GeneralException(ErrorStatus.TOKEN_INVALID);
        }
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
            log.error("Google access token 조회 실패 - 응답: {}", responseBody);
            throw new GeneralException(ErrorStatus.GOOGLE_ACCESS_TOKEN_NULL);
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

    private void saveRefreshToken(Integer userId, String refreshToken) {
        String key = "refresh_token:" + userId;
        redisTemplate.opsForValue().set(
                key,
                refreshToken,
                jwtUtil.getRefreshExpiration(),
                TimeUnit.MILLISECONDS
        );
        log.debug("Refresh Token 저장 완료 - userId: {}", userId);
    }

    private String getRefreshToken(Integer userId) {
        String key = "refresh_token:" + userId;
        Object token = redisTemplate.opsForValue().get(key);
        return token != null ? token.toString() : null;
    }

    private void deleteRefreshToken(Integer userId) {
        String key = "refresh_token:" + userId;
        redisTemplate.delete(key);
        log.debug("Refresh Token 삭제 완료 - userId: {}", userId);
    }

    private void addToBlacklist(String token) {
        long expiration = jwtUtil.getExpiration(token);
        if (expiration > 0) {
            String key = "blacklist:" + token;
            redisTemplate.opsForValue().set(key, "logout", expiration, TimeUnit.MILLISECONDS);
            log.debug("Access Token 블랙리스트 추가 완료");
        }
    }

    public boolean isBlacklisted(String token) {
        String key = "blacklist:" + token;
        return redisTemplate.hasKey(key);
    }
}
