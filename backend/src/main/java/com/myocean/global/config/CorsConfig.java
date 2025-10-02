package com.myocean.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    // WebMvcConfigurer 방식은 제거하고 Bean 방식만 사용

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 특정 origin만 허용 (credentials=true일 때 "*" 사용 불가)
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("http://localhost:8080");
        configuration.addAllowedOrigin("https://myocean.cloud");
        configuration.addAllowedOrigin("https://be.myocean.cloud");
        configuration.addAllowedOrigin("https://j13a303.p.ssafy.io");

        // 허용할 HTTP 메서드
        configuration.addAllowedMethod("*");

        // 허용할 헤더
        configuration.addAllowedHeader("*");

        // 인증 정보 포함 허용 (쿠키 전송을 위해 true로 설정)
        configuration.setAllowCredentials(true);

        // Preflight 요청 캐시 시간
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}