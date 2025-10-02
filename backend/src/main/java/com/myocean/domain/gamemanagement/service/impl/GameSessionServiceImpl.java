package com.myocean.domain.gamemanagement.service.impl;

import com.myocean.domain.gamemanagement.dto.request.GameSessionCreateRequest;
import com.myocean.domain.gamemanagement.dto.response.GameSessionResponse;
import com.myocean.domain.gamemanagement.dto.response.GameGngResultResponse;
import com.myocean.domain.gamemanagement.dto.response.GameUgResultResponse;
import com.myocean.domain.gamemanagement.dto.response.GameBartResultResponse;
import com.myocean.domain.gamemanagement.dto.converter.GameSessionConverter;
import com.myocean.domain.gamemanagement.entity.GameSession;
import com.myocean.domain.gamemanagement.repository.GameSessionRepository;
import com.myocean.domain.gamemanagement.repository.GameSessionResultRepository;
import com.myocean.domain.gamemanagement.service.GameSessionService;
import com.myocean.domain.gamemanagement.enums.GameType;
import com.myocean.domain.gng.entity.GameGngResult;
import com.myocean.domain.gng.repository.GameGngResultRepository;
import com.myocean.domain.gng.dto.converter.GngResultConverter;
import com.myocean.domain.user.service.GameCountService;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GameSessionServiceImpl implements GameSessionService {

    private final GameSessionRepository gameSessionRepository;
    private final GameSessionResultRepository gameSessionResultRepository;
    private final GameGngResultRepository gameGngResultRepository;
    private final GameCountService gameCountService;

    @Override
    @Transactional
    public GameSessionResponse createGameSession(Integer userId, GameSessionCreateRequest request) {
        GameSession gameSession = GameSession.builder()
                .userId(userId)
                .gameType(request.gameType())
                .startedAt(LocalDateTime.now())
                .build();

        GameSession savedSession = gameSessionRepository.save(gameSession);

        return GameSessionConverter.toResponse(savedSession);
    }

    @Override
    public Object getGameSessionResult(Integer userId, Long sessionId) {

        GameType gameType = gameSessionRepository.findGameTypeBySessionId(sessionId, userId);

        switch (gameType) {
            case GNG:
                GameGngResult gngResult = gameGngResultRepository
                        .findById(sessionId)
                        .orElseThrow(() -> new GeneralException(ErrorStatus.GAME_SESSION_RESULT_NOT_FOUND));

                return GngResultConverter.toResponse(gngResult);

            case UG:
                // 나중
                return new GameUgResultResponse();

            case BART:
                // 나중
                return new GameBartResultResponse();

            default:
                throw new GeneralException(ErrorStatus.GAME_SESSION_RESULT_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public GameSessionResponse finishGameSession(Integer userId, Long sessionId) {
        GameSession gameSession = gameSessionRepository
                .findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.GAME_SESSION_NOT_FOUND));

        if (gameSession.getFinishedAt() != null) {
            throw new GeneralException(ErrorStatus.GAME_SESSION_ALREADY_FINISHED);
        }

        gameSession.setFinishedAt(LocalDateTime.now());
        GameSession updatedSession = gameSessionRepository.save(gameSession);

        // Redis 게임 카운트 증가
        gameCountService.incrementGameCount(userId, gameSession.getGameType());

        return GameSessionConverter.toResponse(updatedSession);
    }
}