package com.myocean.domain.user.dto.response;

import com.myocean.domain.user.enums.AiStatus;
import com.myocean.domain.user.enums.Provider;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 정보 응답")
public record UserResponse(
        @Schema(description = "유저 ID", example = "1")
        Integer id,

        @Schema(description = "이메일", example = "user@example.com")
        String email,

        @Schema(description = "OAuth 제공자", example = "GOOGLE")
        Provider provider,

        @Schema(description = "닉네임", example = "바다탐험가")
        String nickname,

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
        String profileImageUrl,

        @Schema(description = "AI 상태", example = "UNSET")
        AiStatus aiStatus
) {
}