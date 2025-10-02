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

        @Schema(description = "개방성 점수", example = "75")
        Integer resultO,

        @Schema(description = "성실성 점수", example = "82")
        Integer resultC,

        @Schema(description = "외향성 점수", example = "68")
        Integer resultE,

        @Schema(description = "친화성 점수", example = "90")
        Integer resultA,

        @Schema(description = "신경성 점수", example = "45")
        Integer resultN
) {
    public static GameSessionResultResponse from(Long sessionId, Integer userId, GameType gameType,
                                               SessionType sessionType, LocalDateTime startedAt,
                                               LocalDateTime finishedAt, Integer resultO, Integer resultC,
                                               Integer resultE, Integer resultA, Integer resultN) {
        return new GameSessionResultResponse(
                sessionId, userId, gameType, sessionType,
                startedAt, finishedAt,
                resultO, resultC, resultE, resultA, resultN
        );
    }
}