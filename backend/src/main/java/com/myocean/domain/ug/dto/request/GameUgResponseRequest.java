package com.myocean.domain.ug.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "UG 게임 응답 제출 요청")
public record GameUgResponseRequest(
        @Schema(description = "오더 ID (선택사항, 백엔드에서 자동 계산)", example = "1")
        Long orderId,

        @Schema(description = "총 금액 (프론트에서 생성한 랜덤 값)", example = "50000", required = true)
        @NotNull(message = "총 금액은 필수입니다")
        @PositiveOrZero(message = "총 금액은 0 이상이어야 합니다")
        Integer totalAmount,

        @Schema(description = "수락 여부 (Role 2에서만 필요)", example = "true")
        Boolean isAccepted,

        @Schema(description = "제안 비율 (Role 1,3에서만 필요, 1-9)", example = "5")
        @Min(value = 1, message = "제안 비율은 1 이상이어야 합니다")
        @Max(value = 9, message = "제안 비율은 9 이하여야 합니다")
        Integer proposalRate
) {
}