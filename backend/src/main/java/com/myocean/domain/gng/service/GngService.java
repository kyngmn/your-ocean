package com.myocean.domain.gng.service;

import com.myocean.domain.gamesession.entity.GameSession;
import com.myocean.domain.gamesession.repository.GameSessionRepository;
import com.myocean.domain.gng.dto.request.GngResponseCreateRequest;
import com.myocean.domain.gng.entity.GameGngResponse;
import com.myocean.domain.gng.repository.GameGngResponseRepository;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GngService {

    private final GameGngResponseRepository gameGngResponseRepository;
    private final GameSessionRepository gameSessionRepository;
    private final GngCalculationService gngCalculationService;

    @Transactional
    public void saveGngResponse(Integer userId, Long sessionId, Integer roundIndex, GngResponseCreateRequest request) {

        // 라운드 번호 유효성 검증
        if (roundIndex < 1 || roundIndex > 50) {
            throw new GeneralException(ErrorStatus.GNG_INVALID_ROUND_INDEX);
        }

        // 게임 세션이 존재하고 해당 사용자의 것인지 검증
        GameSession gameSession = gameSessionRepository
                .findByIdAndUser_Id(sessionId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.GAME_SESSION_NOT_FOUND));

        // GNG 게임인지 검증
        if (gameSession.getGameType() != com.myocean.domain.gamesession.enums.GameType.GNG) {
            throw new GeneralException(ErrorStatus.GNG_SESSION_NOT_GNG_GAME);
        }

        try {
            // GNG 응답 엔티티 생성
            GameGngResponse gngResponse = GameGngResponse.builder()
                    .sessionId(sessionId)
                    .round(roundIndex.shortValue())
                    .stimulusType(request.stimulusType())
                    .stimulusStartedAt(request.stimulusStartedAt())
                    .respondedAt(request.respondedAt())
                    .isSucceeded(request.isSucceeded())
                    .build();

            GameGngResponse savedResponse = gameGngResponseRepository.save(gngResponse);

            // 50라운드 완료 체크
            if (roundIndex == 50) {
                gngCalculationService.calculateAndSaveGameResult(sessionId);
            }
        } catch (GeneralException e) {
            // 이미 정의된 비즈니스 예외는 그대로 전파
            throw e;
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.GNG_RESPONSE_SAVE_FAILED);
        }
    }
}