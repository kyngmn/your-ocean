package com.myocean.domain.bart.dto.request;

import com.myocean.domain.bart.enums.BalloonColor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

@Schema(description = "BART 게임 클릭 기록 요청")
public record GameBartClickRequest(
        @Schema(description = "풍선 색상 (최초 클릭 시에만 필요)", example = "RED")
        BalloonColor color,

        @Schema(description = "풍선 터지는 지점 (최초 클릭 시에만 필요)", example = "15")
        @PositiveOrZero
        Integer poppingPoint,

        @Schema(description = "클릭 인덱스 (1부터 시작)", example = "1", required = true)
        @NotNull(message = "클릭 인덱스는 필수입니다")
        @Positive(message = "클릭 인덱스는 1 이상이어야 합니다")
        Integer clickIndex,

        @Schema(description = "클릭한 시간", example = "2024-01-15T10:30:15", required = true)
        @NotNull(message = "클릭 시간은 필수입니다")
        LocalDateTime clickedAt
) {
}