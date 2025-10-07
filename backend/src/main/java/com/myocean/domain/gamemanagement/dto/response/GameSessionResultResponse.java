package com.myocean.domain.gamemanagement.dto.response;

import com.myocean.domain.gamemanagement.enums.GameType;
import com.myocean.domain.gamemanagement.enums.SessionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "게임 세션 결과 응답")
public record GameSessionResultResponse(
        @Schema(description = "세션 ID", example = "1")
        Long sessionId,

        @Schema(description = "사용자 ID", example = "123")
        Integer userId,

        @Schema(description = "게임 타입", example = "BART")
        GameType gameType,

        @Schema(description = "세션 타입", example = "PRACTICE")
        SessionType sessionType,

        @Schema(description = "시작 시간", example = "2024-01-15T10:30:00")
        LocalDateTime startedAt,

        @Schema(description = "종료 시간", example = "2024-01-15T11:00:00")
        LocalDateTime finishedAt,

        @Schema(description = "게임 결과 데이터 (게임별로 다른 구조)")
        Object gameResult
) {
    public static GameSessionResultResponse from(Long sessionId, Integer userId, GameType gameType,
                                               SessionType sessionType, LocalDateTime startedAt,
                                               LocalDateTime finishedAt, Object gameResult) {
        return new GameSessionResultResponse(
                sessionId, userId, gameType, sessionType,
                startedAt, finishedAt, gameResult
        );
    }
}