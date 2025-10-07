package com.myocean.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // 보안 스키마 정의
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        SecurityScheme cookieAuth = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .name("accessToken");

        // 보안 요구사항
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearerAuth")
                .addList("cookieAuth");

        return new OpenAPI()
                .servers(List.of(
                        new Server().url("https://j13a303.p.ssafy.io").description("SSAFY Production Server"),
                        new Server().url("http://localhost:8080").description("Local Development Server")
                ))
                .info(new Info()
                        .title("MyOcean API")
                        .version("1.0.0")
                        .description("MyOcean 프로젝트 API 문서\n\n" +
                                "## 인증 방법\n" +
                                "1. **Cookie 인증**: 로그인 후 자동으로 쿠키가 설정됩니다.\n" +
                                "2. **Bearer Token**: JWT 토큰을 Authorization 헤더에 입력하세요.\n\n" +
                                "**로그인**: [/login.html](./login.html)")
                        .contact(new Contact()
                                .name("MyOcean Team")
                                .email("parkdanilssafy@gmail.com")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", bearerAuth)
                        .addSecuritySchemes("cookieAuth", cookieAuth))
                .addSecurityItem(securityRequirement);
    }
}