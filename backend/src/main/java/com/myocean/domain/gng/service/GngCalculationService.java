package com.myocean.domain.gng.service;

import com.myocean.domain.big5.service.Big5GNGCalculationService;
import com.myocean.domain.gamesession.entity.GameSession;
import com.myocean.domain.gamesession.repository.GameSessionRepository;
import com.myocean.domain.gng.entity.GameGngResponse;
import com.myocean.domain.gng.entity.GameGngResult;
import com.myocean.domain.gng.enums.GngStimulus;
import com.myocean.domain.gng.repository.GameGngResponseRepository;
import com.myocean.domain.gng.repository.GameGngResultRepository;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GngCalculationService {

    private final GameGngResponseRepository gameGngResponseRepository;
    private final GameGngResultRepository gameGngResultRepository;
    private final GameSessionRepository gameSessionRepository;
    private final Big5GNGCalculationService big5GNGCalculationService;

    @Transactional
    public void calculateAndSaveGameResult(Long sessionId) {

        try {
            // 해당 세션의 모든 응답 데이터 조회
            List<GameGngResponse> responses = gameGngResponseRepository.findBySessionId(sessionId);

            if (responses.isEmpty()) {
                throw new GeneralException(ErrorStatus.GNG_RESPONSES_NOT_FOUND);
            }

            // 1. 총 정답/오답 카운트
            long totalCorrect = responses.stream()
                    .mapToLong(r -> Boolean.TRUE.equals(r.getIsSucceeded()) ? 1 : 0)
                    .sum();
            long totalIncorrect = responses.stream()
                    .mapToLong(r -> Boolean.FALSE.equals(r.getIsSucceeded()) ? 1 : 0)
                    .sum();

            // 2. NOGO 억제 실패 카운트 (NOGO 자극에서 실패한 경우)
            long nogoIncorrect = responses.stream()
                    .filter(r -> r.getStimulusType() == GngStimulus.NOGO)
                    .mapToLong(r -> Boolean.FALSE.equals(r.getIsSucceeded()) ? 1 : 0)
                    .sum();

            // 3. GO 정답의 평균 반응시간 계산 (밀리초)
            double avgReactionTime = responses.stream()
                    .filter(r -> r.getStimulusType() == GngStimulus.GO)
                    .filter(r -> Boolean.TRUE.equals(r.getIsSucceeded()))
                    .filter(r -> r.getRespondedAt() != null)
                    .mapToLong(r -> Duration.between(r.getStimulusStartedAt(), r.getRespondedAt()).toMillis())
                    .average()
                    .orElse(0.0);

            // 게임 세션 정보 조회
            GameSession gameSession = gameSessionRepository.findById(sessionId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.GAME_SESSION_NOT_FOUND));

            // 4. 게임 결과 저장
            GameGngResult result = GameGngResult.builder()
                    .sessionId(sessionId)
                    .totalCorrectCnt((int) totalCorrect)
                    .totalIncorrectCnt((int) totalIncorrect)
                    .nogoIncorrectCnt((int) nogoIncorrect)
                    .avgReactionTime(BigDecimal.valueOf(avgReactionTime))
                    .playedAt(gameSession.getStartedAt())
                    .finishedAt(LocalDateTime.now())
                    .build();

            try {
                gameGngResultRepository.save(result);
            } catch (Exception e) {
                throw new GeneralException(ErrorStatus.GNG_RESULT_SAVE_FAILED);
            }

            //5. 세션 종료 post 요청을 받기 위해 이건 없애는 방향으로 바꿈
//            // 5. GameSession finished_at 업데이트
//            gameSession.setFinishedAt(LocalDateTime.now());
//            gameSessionRepository.save(gameSession);

            // 6. BIG5 지표 계산 및 저장
            big5GNGCalculationService.calculateAndSaveFromGngGame(sessionId, responses);

        } catch (GeneralException e) {  // 이미 정의된 예외는 그대로 전파
            throw e;
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.GNG_CALCULATION_FAILED);
        }
    }
}