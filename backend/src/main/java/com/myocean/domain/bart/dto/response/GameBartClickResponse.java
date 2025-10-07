package com.myocean.domain.bart.dto.response;

import com.myocean.domain.bart.enums.BalloonColor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "BART 게임 클릭 기록 응답")
public record GameBartClickResponse(
        @Schema(description = "클릭 ID", example = "123")
        Long clickId,

        @Schema(description = "응답 ID", example = "45")
        Long responseId,

        @Schema(description = "세션 ID", example = "1")
        Long sessionId,

        @Schema(description = "라운드 번호", example = "0")
        Integer roundIndex,

        @Schema(description = "풍선 색상", example = "RED")
        BalloonColor color,

        @Schema(description = "클릭 인덱스", example = "0")
        Integer clickIndex,

        @Schema(description = "클릭한 시간", example = "2024-01-15T10:30:15")
        LocalDateTime clickedAt,

        @Schema(description = "현재 펌프 수", example = "1")
        Integer currentPumpCount,

        @Schema(description = "풍선이 터졌는지 여부", example = "false")
        Boolean balloonPopped,

        @Schema(description = "획득 금액 (터졌을 경우 0)", example = "50")
        Integer earnedAmount
) {
}