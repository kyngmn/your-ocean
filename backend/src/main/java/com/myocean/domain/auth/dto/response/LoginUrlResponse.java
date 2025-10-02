package com.myocean.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Google 로그인 URL 응답")
public record LoginUrlResponse(
        @Schema(description = "Google 로그인 페이지 URL", example = "https://accounts.google.com/oauth2/auth?client_id=...")
        String loginUrl
) {
}