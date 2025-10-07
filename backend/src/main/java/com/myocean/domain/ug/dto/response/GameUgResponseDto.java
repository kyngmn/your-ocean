package com.myocean.domain.ug.dto.response;

import com.myocean.domain.ug.entity.GameUgResult;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "UG 게임 응답 제출 결과")
public record GameUgResponseDto(
        @Schema(description = "응답 ID", example = "1")
        Long id,

        @Schema(description = "세션 ID", example = "1")
        Long sessionId,

        @Schema(description = "라운드 ID", example = "1")
        Integer round,

        @Schema(description = "오더 ID", example = "1")
        Long orderId,

        @Schema(description = "제안 금액", example = "5000")
        Integer money,

        @Schema(description = "수락 여부", example = "true")
        Boolean isAccepted,

        @Schema(description = "제안 비율", example = "5")
        Integer proposalRate,

        @Schema(description = "제출 시간", example = "2024-01-15T10:30:00")
        LocalDateTime finishedAt,

        @Schema(description = "게임 완료 여부", example = "false")
        Boolean isGameCompleted,

        @Schema(description = "게임 결과 (게임 완료 시에만 포함)", nullable = true)
        GameUgResult gameResult
) {
}