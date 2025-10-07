package com.myocean.domain.gamesession.service;

import com.myocean.domain.bart.converter.BartResultConverter;
import com.myocean.domain.bart.repository.GameBartResultRepository;
import com.myocean.domain.gamesession.dto.request.GameSessionCreateRequest;
import com.myocean.domain.gamesession.dto.response.GameSessionResponse;
import com.myocean.domain.gamesession.dto.converter.GameSessionConverter;
import com.myocean.domain.gamesession.entity.GameSession;
import com.myocean.domain.gamesession.repository.GameSessionRepository;
import com.myocean.domain.gamesession.enums.GameType;
import com.myocean.domain.gng.repository.GameGngResultRepository;
import com.myocean.domain.gng.dto.converter.GngResultConverter;
import com.myocean.domain.ug.repository.GameUgResultRepository;
import com.myocean.domain.ug.dto.converter.UgResultConverter;
import com.myocean.domain.user.service.UserGameCountService;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GameSessionService {

    private final GameSessionRepository gameSessionRepository;
    private final GameGngResultRepository gameGngResultRepository;
    private final GameUgResultRepository gameUgResultRepository;
    private final GameBartResultRepository gameBartResultRepository;
    private final UserGameCountService userGameCountService;

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

    public Object getGameSessionResult(Integer userId, Long sessionId) {
        GameType gameType = gameSessionRepository.findGameTypeBySessionId(sessionId, userId);

        return switch (gameType) {
            case GNG -> GngResultConverter.toResponse(
                    findResultById(gameGngResultRepository, sessionId)
            );
            case UG -> UgResultConverter.toResponse(
                    findResultById(gameUgResultRepository, sessionId)
            );
            case BART -> BartResultConverter.toResponse(
                    findResultById(gameBartResultRepository, sessionId)
            );
        };
    }

    @Transactional
    public GameSessionResponse finishGameSession(Integer userId, Long sessionId) {
        GameSession gameSession = gameSessionRepository
                .findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.GAME_SESSION_NOT_FOUND));

        try {
            gameSession.finish();
        } catch (IllegalStateException e) {
            throw new GeneralException(ErrorStatus.GAME_SESSION_ALREADY_FINISHED);
        }

        GameSession updatedSession = gameSessionRepository.save(gameSession);

        // Redis 게임 카운트 증가
        userGameCountService.incrementGameCount(userId, gameSession.getGameType());
        return GameSessionConverter.toResponse(updatedSession);
    }

    private <T> T findResultById(JpaRepository<T, Long> repository, Long sessionId) {
        return repository.findById(sessionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.GAME_SESSION_RESULT_NOT_FOUND));
    }
}