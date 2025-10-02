package com.myocean.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "유저 정보 수정 요청")
public record UpdateUserRequest(
        @Schema(description = "사용자 닉네임", example = "바다탐험가")
        @NotBlank(message = "닉네임은 필수입니다")
        @Size(min = 1, max = 10, message = "닉네임은 1자 이상 10자 이하여야 합니다")
        String nickname,

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
        String profileImageUrl
) {
}