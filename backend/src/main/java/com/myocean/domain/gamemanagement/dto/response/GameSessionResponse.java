package com.myocean.domain.gamemanagement.dto.response;

import com.myocean.domain.gamemanagement.enums.GameType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "게임 세션 응답")
public record GameSessionResponse(
        @Schema(description = "세션 ID", example = "1")
        Long sessionId,

        @Schema(description = "사용자 ID", example = "123")
        Integer userId,

        @Schema(description = "게임 타입", example = "BART")
        GameType gameType,

        @Schema(description = "시작 시간", example = "2024-01-15T10:30:00")
        LocalDateTime startedAt,

        @Schema(description = "종료 시간", example = "2024-01-15T11:00:00")
        LocalDateTime finishedAt,

        @Schema(description = "세션 완료 여부", example = "false")
        Boolean isFinished
) {
    public static GameSessionResponse from(Long sessionId, Integer userId, GameType gameType,
                                         LocalDateTime startedAt, LocalDateTime finishedAt) {
        return new GameSessionResponse(
                sessionId,
                userId,
                gameType,
                startedAt,
                finishedAt,
                finishedAt != null
        );
    }
}