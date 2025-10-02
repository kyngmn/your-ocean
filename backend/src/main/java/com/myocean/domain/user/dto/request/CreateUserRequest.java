package com.myocean.domain.user.dto.request;

import com.myocean.domain.user.enums.Provider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "유저 생성 요청")
public record CreateUserRequest(
        @Schema(description = "이메일", example = "user@example.com")
        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "유효한 이메일 형식이어야 합니다")
        String email,

        @Schema(description = "OAuth 제공자", example = "GOOGLE")
        @NotNull(message = "OAuth 제공자는 필수입니다")
        Provider provider,

        @Schema(description = "소셜 ID", example = "123456789")
        @NotBlank(message = "소셜 ID는 필수입니다")
        String socialId,

        @Schema(description = "사용자 닉네임", example = "바다탐험가")
        @NotBlank(message = "닉네임은 필수입니다")
        @Size(min = 1, max = 10, message = "닉네임은 1자 이상 10자 이하여야 합니다")
        String nickname,

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
        String profileImageUrl
) {
}