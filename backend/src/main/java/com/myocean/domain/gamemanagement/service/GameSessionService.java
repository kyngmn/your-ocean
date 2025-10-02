package com.myocean.domain.gamemanagement.service;

import com.myocean.domain.gamemanagement.dto.request.GameSessionCreateRequest;
import com.myocean.domain.gamemanagement.dto.response.GameSessionResponse;
import com.myocean.domain.gamemanagement.dto.response.GameSessionResultResponse;

public interface GameSessionService {

    GameSessionResponse createGameSession(Integer userId, GameSessionCreateRequest request);

    Object getGameSessionResult(Integer userId, Long sessionId);

    GameSessionResponse finishGameSession(Integer userId, Long sessionId);
}