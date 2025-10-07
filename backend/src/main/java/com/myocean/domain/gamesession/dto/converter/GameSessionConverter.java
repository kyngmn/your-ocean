package com.myocean.domain.gamesession.dto.converter;

import com.myocean.domain.gamesession.dto.response.GameSessionResponse;
import com.myocean.domain.gamesession.entity.GameSession;

public class GameSessionConverter {

    public static GameSessionResponse toResponse(GameSession gameSession) {
        return new GameSessionResponse(
                gameSession.getId(),
                gameSession.getUserId(),
                gameSession.getGameType(),
                gameSession.getStartedAt(),
                gameSession.getFinishedAt(),
                gameSession.getFinishedAt() != null
        );
    }
}