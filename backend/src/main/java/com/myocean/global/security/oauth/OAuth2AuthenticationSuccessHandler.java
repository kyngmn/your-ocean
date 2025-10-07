package com.myocean.global.security.oauth;

import com.myocean.domain.user.entity.User;
import com.myocean.domain.user.enums.Provider;
import com.myocean.domain.user.repository.UserRepository;
import com.myocean.domain.report.repository.ReportRepository;
import com.myocean.global.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        log.info("=== OAuth2 Success Handler 시작 ===");
        log.info("🔄 요청 URL: {}", request.getRequestURL().toString());
        log.info("🔄 요청 Query String: {}", request.getQueryString());
        log.info("🔄 요청 Method: {}", request.getMethod());
        log.info("🔄 User-Agent: {}", request.getHeader("User-Agent"));
        log.info("🔄 Referer: {}", request.getHeader("Referer"));

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String socialId = oauth2User.getAttribute("sub"); // Google의 고유 ID

        log.info("👤 OAuth2 사용자 정보 - Email: {}, Name: {}, SocialId: {}", email, name, socialId);

        // 사용자 찾기 또는 생성
        User user = findOrCreateUser(email, name, socialId);

        // JWT 토큰 생성
        log.info("🔐 JWT 토큰 생성 시작 - 사용자 ID: {}, Email: {}", user.getId(), user.getEmail());
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        log.info("🔐 JWT 토큰 생성 완료 - 토큰 길이: {}, 토큰 시작: {}...", token.length(), token.substring(0, 20));

        // 실제 접속한 호스트 정보 추출 (help 프로젝트 방식 적용)
        String scheme = request.getScheme(); // http 또는 https
        String host = request.getHeader("Host"); // 실제 접속한 도메인:포트

        // 환경변수 설정된 FRONTEND_URL을 그대로 사용
        log.info("🔧 환경변수 FRONTEND_URL: {}", frontendUrl);
        log.info("🔧 시스템 환경변수 FRONTEND_URL: {}", System.getenv("FRONTEND_URL"));
        log.info("🔧 현재 요청 호스트: {}", host);
        log.info("🔧 현재 요청 스키마: {}", scheme);
        String actualFrontendUrl = frontendUrl;
        log.info("🌐 설정된 FRONTEND_URL 사용: {}", actualFrontendUrl);

        // 쿠키 설정 제거 - 프론트엔드에서 URL 파라미터의 토큰을 직접 처리
        log.info("🍪 쿠키 설정 생략 - 프론트엔드에서 URL 파라미터로 토큰 처리 예정");

        // reports 테이블에 해당 사용자의 레코드가 있는지 확인
        log.info("📊 사용자 리포트 확인 - 사용자 ID: {}", user.getId());
        boolean hasReports = reportRepository.existsByUserIdAndReportType(user.getId(), com.myocean.domain.report.enums.ReportType.SELF);
        log.info("📊 사용자 리포트 존재 여부: {}", hasReports);

        // 프론트엔드로 리다이렉트 (토큰을 URL 파라미터와 쿠키 모두로 전달)
        String callbackPath = "/handler/auth/callback";
        log.info("🔗 리다이렉트 URL 생성 시작 - Frontend URL: {}, Callback Path: {}", actualFrontendUrl, callbackPath);

        String targetUrl = UriComponentsBuilder.fromUriString(actualFrontendUrl + callbackPath)
                .queryParam("token", token)  // 토큰을 URL 파라미터로도 전달 (백업용)
                .queryParam("new", !hasReports)
                .build().toUriString();

        log.info("🔗 최종 리다이렉트 URL: {}", targetUrl);
        log.info("🔗 파라미터 - token: {}..., new: {}", token.substring(0, 20), !hasReports);
        log.info("✅ OAuth2 인증 성공 완료 - 사용자: {}, 리다이렉트 실행", user.getEmail());

        try {
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
            log.info("✅ 리다이렉트 전송 성공");
        } catch (Exception e) {
            log.error("❌ 리다이렉트 전송 실패: {}", e.getMessage(), e);
        }
    }

    private User findOrCreateUser(String email, String name, String socialId) {
        log.info("👤 사용자 찾기/생성 시작 - Email: {}, Name: {}, SocialId: {}", email, name, socialId);
        Optional<User> existingUser = userRepository.findByProviderAndSocialId(Provider.GOOGLE, socialId);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            log.info("👤 기존 사용자 발견 - ID: {}, Email: {}, Nickname: {}", user.getId(), user.getEmail(), user.getNickname());
            return user;
        }

        log.info("👤 새 사용자 생성 필요 - 닉네임 생성 시작");
        // 새 사용자 생성 - 임시 닉네임 설정 (10자 제한)
        String tempNickname;
        if (name != null && name.length() <= 10) {
            tempNickname = name;
            log.info("👤 닉네임 생성 - 원본 이름 사용: {}", tempNickname);
        } else if (name != null && name.length() > 10) {
            tempNickname = name.substring(0, 10);
            log.info("👤 닉네임 생성 - 이름 단축: {} -> {}", name, tempNickname);
        } else {
            // 랜덤 6자리 숫자로 짧은 닉네임 생성
            tempNickname = "사용자" + (int)(Math.random() * 900000 + 100000);
            log.info("👤 닉네임 생성 - 랜덤 생성: {}", tempNickname);
        }

        User newUser = User.builder()
                .email(email)
                .provider(Provider.GOOGLE)
                .socialId(socialId)
                .nickname(tempNickname)
                .build();

        log.info("👤 새 사용자 객체 생성 완료 - Email: {}, Nickname: {}", email, tempNickname);
        User savedUser = userRepository.save(newUser);
        log.info("👤 새 사용자 저장 완료 - ID: {}", savedUser.getId());
        return savedUser;
    }
}