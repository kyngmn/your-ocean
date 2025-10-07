package com.myocean.domain.bart.dto.response;

import com.myocean.domain.bart.enums.BalloonColor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "BART 게임 라운드 종료 응답")
public record GameBartRoundResponse(
        @Schema(description = "응답 ID", example = "45")
        Long responseId,

        @Schema(description = "세션 ID", example = "1")
        Long sessionId,

        @Schema(description = "라운드 번호", example = "0")
        Integer roundIndex,

        @Schema(description = "풍선 색상", example = "RED")
        BalloonColor color,

        @Schema(description = "풍선 터지는 지점", example = "15")
        Integer poppingPoint,

        @Schema(description = "풍선이 터졌는지 여부", example = "false")
        Boolean isPopped,

        @Schema(description = "총 펌프 횟수", example = "12")
        Integer pumpingCnt,

        @Schema(description = "라운드 시작 시간", example = "2024-01-15T10:30:00")
        LocalDateTime playedAt,

        @Schema(description = "라운드 종료 시간", example = "2024-01-15T10:31:00")
        LocalDateTime finishedAt
) {
}