package com.myocean.domain.gamesession.converter;

import com.myocean.domain.gamesession.dto.response.GameSessionResponse;
import com.myocean.domain.gamesession.entity.GameSession;

public class GameSessionConverter {

    public static GameSessionResponse toResponse(GameSession gameSession) {
        return GameSessionResponse.builder()
                .sessionId(gameSession.getId())
                .userId(gameSession.getUser().getId())
                .gameType(gameSession.getGameType())
                .startedAt(gameSession.getStartedAt())
                .finishedAt(gameSession.getFinishedAt())
                .isFinished(gameSession.getFinishedAt() != null)
                .build();
    }
}