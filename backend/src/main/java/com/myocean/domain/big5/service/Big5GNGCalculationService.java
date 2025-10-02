package com.myocean.domain.big5.service;

import com.myocean.domain.big5.dto.request.Big5ResultCreateRequest;
import com.myocean.domain.big5.entity.Big5Result;
import com.myocean.domain.big5.enums.Big5SourceType;
import com.myocean.domain.big5.repository.Big5ResultRepository;
import com.myocean.domain.gamemanagement.entity.GameSession;
import com.myocean.domain.gamemanagement.repository.GameSessionRepository;
import com.myocean.domain.gng.entity.GameGngResponse;
import com.myocean.domain.gng.enums.GngStimulus;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class Big5GNGCalculationService {

    private final Big5ResultRepository big5ResultRepository;
    private final GameSessionRepository gameSessionRepository;

    private static final double OPTIMAL_REACTION_TIME = 400.0; // ms
    private static final double MAX_REACTION_TIME_DEVIATION = 400.0; // ms

    public void calculateAndSaveFromGngGame(Long sessionId, List<GameGngResponse> responses) {
        try {
            // 데이터 유효성 검증
            if (responses == null || responses.isEmpty()) {
                throw new GeneralException(ErrorStatus.BIG5_INSUFFICIENT_DATA);
            }

            // GameSession 조회하여 userId 가져오기
            GameSession gameSession = gameSessionRepository.findById(sessionId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.GAME_SESSION_NOT_FOUND));

            // Big5 계산
            Big5ResultCreateRequest big5Request = calculateFromGngGame(sessionId, responses);

            // Big5Result 엔티티 생성 및 저장
            Big5Result big5Result = Big5Result.builder()
                    .userId(gameSession.getUserId())
                    .sourceType(big5Request.getSourceType())
                    .sourceId(big5Request.getSourceId())
                    .resultO(big5Request.getOpenness())
                    .resultC(big5Request.getConscientiousness())
                    .resultE(big5Request.getExtraversion())
                    .resultA(big5Request.getAgreeableness())
                    .resultN(big5Request.getNeuroticism())
                    .build();

            big5ResultRepository.save(big5Result);
        } catch (GeneralException e) {
            // 이미 정의된 비즈니스 예외는 그대로 전파
            throw e;
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.BIG5_RESULT_SAVE_FAILED);
        }
    }

    public Big5ResultCreateRequest calculateFromGngGame(Long sessionId, List<GameGngResponse> responses) {
        try {
            // 데이터 유효성 검증
            if (responses == null || responses.isEmpty()) {
                throw new GeneralException(ErrorStatus.BIG5_INSUFFICIENT_DATA);
            }

            // GNG 게임 응답 통계 계산
            int totalCorrect = (int) responses.stream()
                    .mapToLong(r -> Boolean.TRUE.equals(r.getIsSucceeded()) ? 1 : 0)
                    .sum();

            int totalIncorrect = (int) responses.stream()
                    .mapToLong(r -> Boolean.FALSE.equals(r.getIsSucceeded()) ? 1 : 0)
                    .sum();

            int nogoIncorrect = (int) responses.stream()
                    .filter(r -> r.getStimulusType() == GngStimulus.NOGO)
                    .mapToLong(r -> Boolean.FALSE.equals(r.getIsSucceeded()) ? 1 : 0)
                    .sum();

            double avgReactionTime = responses.stream()
                    .filter(r -> r.getStimulusType() == GngStimulus.GO)
                    .filter(r -> Boolean.TRUE.equals(r.getIsSucceeded()))
                    .filter(r -> r.getRespondedAt() != null)
                    .mapToLong(r -> Duration.between(r.getStimulusStartedAt(), r.getRespondedAt()).toMillis())
                    .average()
                    .orElse(0.0);

            // Big5 점수 계산
            Integer conscientiousness = calculateConscientiousness(totalCorrect, totalIncorrect, nogoIncorrect, avgReactionTime);
            Integer neuroticism = calculateNeuroticism(totalCorrect, totalIncorrect, nogoIncorrect);

            return Big5ResultCreateRequest.builder()
                    .sourceType(Big5SourceType.GAME)
                    .sourceId(sessionId)
                    .openness(null)                 // GNG에서는 측정 불가
                    .conscientiousness(conscientiousness)
                    .extraversion(null)             // GNG에서는 측정 불가
                    .agreeableness(null)            // GNG에서는 측정 불가
                    .neuroticism(neuroticism)
                    .build();

        } catch (GeneralException e) {
            throw e;
        } catch (Exception e) {
            log.error("Big5 calculation failed for session: {}", sessionId, e);
            throw new GeneralException(ErrorStatus.BIG5_CALCULATION_FAILED);
        }
    }

    private Integer calculateConscientiousness(int totalCorrect, int totalIncorrect, int nogoIncorrect, double avgReactionTime) {
        // Go 응답 수 계산 (전체 응답에서 NOGO 응답 제외)
        int goResponses = totalCorrect + totalIncorrect - nogoIncorrect;
        if (goResponses == 0) {
            return null;
        }

        // Go 정확도 계산
        double accuracyGo = (double) totalCorrect / goResponses;

        // 반응시간 점수 계산 (최적 반응시간에 가까울수록 높은 점수)
        double reactionTimeScore = 100 - (Math.abs(avgReactionTime - OPTIMAL_REACTION_TIME) / MAX_REACTION_TIME_DEVIATION * 100);
        reactionTimeScore = Math.max(0, Math.min(reactionTimeScore, 100)); // 0~100 clamp

        // 성실성 점수 = 70% 정확도 + 30% 반응시간 적절성
        double conscientiousness = 0.7 * (accuracyGo * 100) + 0.3 * reactionTimeScore;
        conscientiousness = Math.max(0, Math.min(conscientiousness, 100)); // 0~100 clamp

        return (int) Math.round(conscientiousness);
    }

    private Integer calculateNeuroticism(int totalCorrect, int totalIncorrect, int nogoIncorrect) {
        int totalResponses = totalCorrect + totalIncorrect;
        if (totalResponses == 0) {
            return null;
        }

        // NOGO 실패율
        double nogoFailureRate = (double) nogoIncorrect / totalResponses;

        // 전체 오류율
        double errorRate = (double) totalIncorrect / totalResponses;

        // 신경성 점수 = 60% NOGO 실패율 + 40% 전체 오류율
        double neuroticism = 0.6 * (nogoFailureRate * 100) + 0.4 * (errorRate * 100);
        neuroticism = Math.max(0, Math.min(neuroticism, 100)); // 0~100 clamp

        return (int) Math.round(neuroticism);
    }
}