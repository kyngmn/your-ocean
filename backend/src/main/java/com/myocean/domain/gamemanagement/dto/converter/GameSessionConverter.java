package com.myocean.domain.gamemanagement.dto.converter;

import com.myocean.domain.gamemanagement.dto.response.GameSessionResponse;
import com.myocean.domain.gamemanagement.entity.GameSession;

public class GameSessionConverter {

    public static GameSessionResponse toResponse(GameSession gameSession) {
        return GameSessionResponse.from(
                gameSession.getId(),
                gameSession.getUserId(),
                gameSession.getGameType(),
                gameSession.getStartedAt(),
                gameSession.getFinishedAt()
        );
    }
}