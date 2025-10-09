package com.myocean.domain.gamesession.dto.response;

import com.myocean.domain.gamesession.enums.GameType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "게임 세션 응답")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameSessionResponse {

    @Schema(description = "세션 ID", example = "1")
    private Long sessionId;

    @Schema(description = "사용자 ID", example = "123")
    private Integer userId;

    @Schema(description = "게임 타입", example = "BART")
    private GameType gameType;

    @Schema(description = "시작 시간", example = "2024-01-15T10:30:00")
    private LocalDateTime startedAt;

    @Schema(description = "종료 시간", example = "2024-01-15T11:00:00")
    private LocalDateTime finishedAt;

    @Schema(description = "세션 완료 여부", example = "false")
    private Boolean isFinished;
}