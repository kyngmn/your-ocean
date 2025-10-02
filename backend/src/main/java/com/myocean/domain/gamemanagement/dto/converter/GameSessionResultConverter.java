package com.myocean.domain.gamemanagement.dto.converter;

import com.myocean.domain.gamemanagement.dto.response.GameSessionResultResponse;
import com.myocean.domain.gamemanagement.entity.GameSession;
import com.myocean.domain.gamemanagement.entity.GameSessionResult;

public class GameSessionResultConverter {

    public static GameSessionResultResponse toResponse(GameSessionResult result, GameSession session) {
        return GameSessionResultResponse.from(
                result.getSessionId(),
                result.getUserId(),
                session.getGameType(),
                result.getSessionType(),
                session.getStartedAt(),
                session.getFinishedAt(),
                result.getResultO(),
                result.getResultC(),
                result.getResultE(),
                result.getResultA(),
                result.getResultN()
        );
    }
}