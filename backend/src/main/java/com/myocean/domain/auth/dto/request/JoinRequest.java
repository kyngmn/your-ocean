package com.myocean.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Google 소셜 회원가입 요청")
public record JoinRequest(
        @Schema(description = "Google에서 받은 authorization code", example = "4/P7q7W91a-oMsCeLvIaQm6bTrgtp7")
        @NotBlank(message = "authorization code는 필수입니다")
        String code,

        @Schema(description = "사용자 닉네임", example = "바다탐험가")
        @NotBlank(message = "닉네임은 필수입니다")
        @Size(min = 1, max = 10, message = "닉네임은 1자 이상 10자 이하여야 합니다")
        String nickname
) {
}