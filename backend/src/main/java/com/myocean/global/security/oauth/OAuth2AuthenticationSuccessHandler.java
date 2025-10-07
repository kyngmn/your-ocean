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

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String socialId = oauth2User.getAttribute("sub"); // Google의 고유 ID

        // 사용자 찾기 또는 생성
        User user = findOrCreateUser(email, name, socialId);

        // JWT 토큰 생성
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());

        // 실제 접속한 호스트 정보 추출 (help 프로젝트 방식 적용)
        String scheme = request.getScheme(); // http 또는 https
        String host = request.getHeader("Host"); // 실제 접속한 도메인:포트

        String actualFrontendUrl;

        // 개발 환경인지 확인 (환경변수 기준)
        log.info("🔧 환경변수 FRONTEND_URL: {}", frontendUrl);
        if (frontendUrl.contains("localhost")) {
            // 개발 환경: HTTP 로컬호스트 사용 (HTTPS가 아님)
            actualFrontendUrl = "http://localhost:3000";
            log.info("🏠 개발 환경 감지 - HTTP localhost 사용: {}", actualFrontendUrl);
        } else {
            // 프로덕션 환경: 설정된 HTTPS URL 사용
            actualFrontendUrl = frontendUrl;
            log.info("🏭 프로덕션 환경 - HTTPS URL 사용: {}", actualFrontendUrl);
        }

        // 로컬 개발 환경에서는 쿠키 설정 생략 (크로스 도메인 문제로 인해)
        // 프론트엔드에서 URL 파라미터의 토큰을 받아서 직접 쿠키/localStorage에 저장하도록 함
        if (!host.contains("localhost") && !host.contains("127.0.0.1")) {
            // 프로덕션 환경에서만 쿠키 설정
            Cookie jwtCookie = new Cookie("accessToken", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(86400); // 24시간
            jwtCookie.setSecure(true); // HTTPS에서만 전송
            response.addCookie(jwtCookie);
        }

        // reports 테이블에 해당 사용자의 레코드가 있는지 확인
        boolean hasReports = reportRepository.findByUserIdAndReportType(user.getId(), com.myocean.domain.report.enums.ReportType.SELF).isPresent();

        // 프론트엔드로 리다이렉트 (토큰을 URL 파라미터와 쿠키 모두로 전달)
        String targetUrl = UriComponentsBuilder.fromUriString(actualFrontendUrl + "/api/auth/callback")
                .queryParam("token", token)  // 토큰을 URL 파라미터로도 전달 (백업용)
                .queryParam("new", !hasReports)
                .build().toUriString();

        log.info("OAuth2 success - redirecting to: {}, host: {}", targetUrl, host);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private User findOrCreateUser(String email, String name, String socialId) {
        Optional<User> existingUser = userRepository.findByProviderAndSocialId(Provider.GOOGLE, socialId);

        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        // 새 사용자 생성 - 임시 닉네임 설정 (10자 제한)
        String tempNickname;
        if (name != null && name.length() <= 10) {
            tempNickname = name;
        } else if (name != null && name.length() > 10) {
            tempNickname = name.substring(0, 10);
        } else {
            // 랜덤 6자리 숫자로 짧은 닉네임 생성
            tempNickname = "사용자" + (int)(Math.random() * 900000 + 100000);
        }

        User newUser = User.builder()
                .email(email)
                .provider(Provider.GOOGLE)
                .socialId(socialId)
                .nickname(tempNickname)
                .build();

        return userRepository.save(newUser);
    }
}