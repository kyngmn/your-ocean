package com.myocean.domain.bart.service.impl;

import com.myocean.domain.bart.dto.request.GameBartClickRequest;
import com.myocean.domain.bart.dto.request.GameBartFinishRoundRequest;
import com.myocean.domain.bart.dto.response.GameBartClickResponse;
import com.myocean.domain.bart.dto.response.GameBartRoundResponse;
import com.myocean.domain.bart.entity.GameBartClick;
import com.myocean.domain.bart.entity.GameBartResponse;
import com.myocean.domain.bart.entity.GameBartResult;
import com.myocean.domain.bart.repository.GameBartClickRepository;
import com.myocean.domain.bart.repository.GameBartResponseRepository;
import com.myocean.domain.bart.repository.GameBartResultRepository;
import com.myocean.domain.bart.service.GameBartService;
import com.myocean.domain.big5.service.Big5BARTCalculationService;
import com.myocean.domain.gamemanagement.entity.GameSession;
import com.myocean.domain.gamemanagement.repository.GameSessionRepository;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GameBartServiceImpl implements GameBartService {

    private final GameSessionRepository gameSessionRepository;
    private final GameBartResponseRepository gameBartResponseRepository;
    private final GameBartClickRepository gameBartClickRepository;
    private final GameBartResultRepository gameBartResultRepository;
    private final Big5BARTCalculationService big5BARTCalculationService;

    @Override
    @Transactional
    public GameBartClickResponse recordClick(Integer userId, Long sessionId, Integer roundIndex, GameBartClickRequest request) {
        // BART 클릭 기록

        // 1. 게임 세션 검증
        GameSession gameSession = gameSessionRepository
                .findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.GAME_SESSION_NOT_FOUND));

        if (gameSession.getFinishedAt() != null) {
            throw new GeneralException(ErrorStatus.GAME_SESSION_ALREADY_FINISHED);
        }

        // 2. 라운드 응답 조회 또는 생성
        GameBartResponse bartResponse = gameBartResponseRepository
                .findBySessionIdAndRoundIndex(sessionId, roundIndex)
                .orElseGet(() -> {
                    // 첫 번째 클릭인 경우 새 라운드 생성
                    if (request.color() == null || request.poppingPoint() == null) {
                        throw new GeneralException(ErrorStatus.BART_ROUND_CREATION_DATA_MISSING);
                    }

                    // 새 BART 라운드 생성

                    GameBartResponse newResponse = GameBartResponse.builder()
                            .sessionId(sessionId)
                            .roundIndex(roundIndex)
                            .color(request.color())
                            .poppingPoint(request.poppingPoint())
                            .isPopped(false)
                            .build();

                    return gameBartResponseRepository.save(newResponse);
                });

        // 3. 라운드가 이미 종료되었는지 확인
        if (bartResponse.getFinishedAt() != null) {
            throw new GeneralException(ErrorStatus.BART_ROUND_ALREADY_FINISHED);
        }

        // 4. 중복 클릭 인덱스 확인
        boolean clickExists = gameBartClickRepository
                .existsByResponseIdAndClickIndex(bartResponse.getId(), request.clickIndex());
        if (clickExists) {
            throw new GeneralException(ErrorStatus.BART_CLICK_INDEX_DUPLICATE);
        }

        // 5. 클릭 기록 저장
        GameBartClick bartClick = GameBartClick.builder()
                .responseId(bartResponse.getId())
                .clickIndex(request.clickIndex())
                .clickedAt(request.clickedAt())
                .build();

        GameBartClick savedClick = gameBartClickRepository.save(bartClick);

        // 6. 현재 펌프 수 계산
        Integer currentPumpCount = gameBartClickRepository.countByResponseId(bartResponse.getId());

        // 7. poppingPoint 도달 시 자동으로 라운드 종료 (풍선 터짐)
        boolean balloonPopped = false;
        if (currentPumpCount >= bartResponse.getPoppingPoint()) {
            // 풍선 터짐

            // 풍선 터짐 - 획득 금액 0원
            bartResponse.setIsPopped(true);
            bartResponse.setPumpingCnt(currentPumpCount);
            bartResponse.setFinishedAt(LocalDateTime.now());
            gameBartResponseRepository.save(bartResponse);

            balloonPopped = true;
            // 풍선 터짐으로 라운드 자동 종료
            
            // 풍선 터짐은 finishRound에서 30라운드 완료 체크 수행 (중복 방지)
        }

        // BART 클릭 기록 완료

        return new GameBartClickResponse(
                savedClick.getId(),
                bartResponse.getId(),
                sessionId,
                roundIndex,
                bartResponse.getColor(),
                savedClick.getClickIndex(),
                savedClick.getClickedAt(),
                currentPumpCount,
                balloonPopped,
                balloonPopped ? 0 : currentPumpCount * 10 // 터지면 0원, 아니면 클릭수 × 10원
        );
    }

    @Override
    @Transactional
    public GameBartRoundResponse finishRound(Integer userId, Long sessionId, Integer roundIndex, GameBartFinishRoundRequest request) {
        try {
            // 1. 게임 세션 검증
            GameSession gameSession = gameSessionRepository
                    .findByIdAndUserId(sessionId, userId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.GAME_SESSION_NOT_FOUND));

            if (gameSession.getFinishedAt() != null) {
                throw new GeneralException(ErrorStatus.GAME_SESSION_ALREADY_FINISHED);
            }

            // 2. 라운드 응답 조회
            GameBartResponse bartResponse = gameBartResponseRepository
                    .findBySessionIdAndRoundIndex(sessionId, roundIndex)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.BART_ROUND_NOT_FOUND));

            // 3. 이미 종료된 라운드인지 확인
            if (bartResponse.getFinishedAt() != null) {
                throw new GeneralException(ErrorStatus.BART_ROUND_ALREADY_FINISHED);
            }

            // 4. 라운드 종료 처리
            Integer pumpingCountResult = gameBartClickRepository.countByResponseId(bartResponse.getId());
            int pumpingCount = pumpingCountResult != null ? pumpingCountResult : 0;

        // 5. 논리적 일관성 검증 (경고만, 에러는 발생시키지 않음)
        boolean shouldBePopped = pumpingCount >= bartResponse.getPoppingPoint();
        if (shouldBePopped && !request.isPopped()) {
            log.warn("Logic inconsistency: pumping count {} >= popping point {} but isPopped is false",
                    pumpingCount, bartResponse.getPoppingPoint());
        }
        bartResponse.setIsPopped(request.isPopped());
        bartResponse.setPumpingCnt(pumpingCount);
        bartResponse.setFinishedAt(LocalDateTime.now());

        // 라운드 응답 저장
        GameBartResponse updatedResponse = gameBartResponseRepository.save(bartResponse);

        // 응답 객체 생성
        GameBartRoundResponse response = new GameBartRoundResponse(
                updatedResponse.getId(),
                sessionId,
                roundIndex,
                updatedResponse.getColor(),
                updatedResponse.getPoppingPoint(),
                updatedResponse.getIsPopped(),
                updatedResponse.getPumpingCnt(),
                updatedResponse.getPlayedAt(),
                updatedResponse.getFinishedAt()
        );

        // 30라운드 완료 체크 및 자동 게임 종료 (응답 저장 완료 후)
        // 30라운드 완료 체크 (결과 계산은 일단 제외)
        try {
            int finishedRounds = gameBartResponseRepository.countBySessionIdAndFinishedAtIsNotNull(sessionId);
            if (finishedRounds >= 30) {
                calculateAndSaveBartResults(sessionId);
            }
        } catch (Exception e) {
            // 30라운드 완료 체크 중 에러 발생
        }

        return response;
        } catch (Exception e) {
            log.error("Unexpected error in finishRound for session {}, round {}: {}", sessionId, roundIndex, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void calculateAndSaveBartResults(Long sessionId) {
        try {
            // 이미 결과가 존재하는지 확인
            if (gameBartResultRepository.existsBySessionId(sessionId)) {
                return;
            }

            // 완료된 라운드 수 확인
            int finishedRounds = gameBartResponseRepository.countBySessionIdAndFinishedAtIsNotNull(sessionId);
            if (finishedRounds < 30) {
                return;
            }
            
            // 해당 세션의 모든 BART 응답 조회
            List<GameBartResponse> bartResponses = gameBartResponseRepository.findBySessionId(sessionId);
            
            // 완료된 라운드만 필터링 (NPE 방지)
            List<GameBartResponse> finishedResponses = bartResponses.stream()
                    .filter(response -> response != null && response.getFinishedAt() != null)
                    .toList();

            if (finishedResponses.isEmpty()) {
                return;
            }

            // 결과 계산 (안전한 방식으로)
            int rewardAmount = 0;
            int missedReward = 0;
            int totalBalloons = finishedResponses.size();
            int successBalloons = 0;
            int failBalloons = 0;
            int totalPumps = 0;

            for (GameBartResponse response : finishedResponses) {
                try {
                    // NPE 방지
                    Integer pumpingCnt = response.getPumpingCnt();
                    int pumpCount = (pumpingCnt != null) ? pumpingCnt : 0;
                    int potentialReward = pumpCount * 10;

                    totalPumps += pumpCount;

                    // Boolean NPE 방지
                    Boolean isPopped = response.getIsPopped();
                    boolean popped = (isPopped != null) ? isPopped : false;
                    
                    if (popped) {
                        failBalloons++;
                        missedReward += potentialReward;
                    } else {
                        successBalloons++;
                        rewardAmount += potentialReward;
                    }
                } catch (Exception ex) {
                    // 개별 응답 처리 중 에러 발생시 스킵
                    continue;
                }
            }

            // 평균 펌프 수 계산 (0으로 나누기 방지)
            BigDecimal avgPumps = BigDecimal.ZERO;
            if (totalBalloons > 0) {
                try {
                    avgPumps = BigDecimal.valueOf(totalPumps)
                        .divide(BigDecimal.valueOf(totalBalloons), 2, RoundingMode.HALF_UP);
                } catch (ArithmeticException ex) {
                    avgPumps = BigDecimal.ZERO;
                }
            }

            // 결과 저장
            GameBartResult result = GameBartResult.builder()
                    .sessionId(sessionId)
                    .rewardAmount(rewardAmount)
                    .missedReward(missedReward)
                    .totalBalloons(totalBalloons)
                    .successBalloons(successBalloons)
                    .failBalloons(failBalloons)
                    .avgPumps(avgPumps)
                    .build();

            GameBartResult savedResult = gameBartResultRepository.save(result);
            
            // BART 결과 저장 후 Big5 지표 계산 및 저장
            GameSession gameSession = gameSessionRepository.findById(sessionId).orElse(null);
            if (gameSession != null) {
                calculateAndSaveBig5FromBart(gameSession.getUserId(), sessionId, savedResult);
            }
                    
        } catch (Exception e) {
            log.error("Error in calculateAndSaveBartResults for session {}: {}", sessionId, e.getMessage());
            // 에러 발생시에도 조용히 처리하여 30라운드 완료에 영향 주지 않음
        }
    }

    private void calculateAndSaveBig5FromBart(Integer userId, Long sessionId, GameBartResult bartResult) {
        try {
            // Big5 계산에 필요한 데이터 조회
            List<GameBartResponse> responses = gameBartResponseRepository.findBySessionId(sessionId);
            
            // 현재 세션의 클릭 데이터 조회
            List<Long> responseIds = responses.stream()
                    .map(GameBartResponse::getId)
                    .toList();
            List<GameBartClick> clicks = gameBartClickRepository.findByResponseIdIn(responseIds);
            
            // 정규화를 위한 전체 데이터 조회 (간소화)
            List<GameBartResult> allResults = gameBartResultRepository.findAll();
            Map<Long, List<GameBartResponse>> allResponses = Map.of(sessionId, responses);
            Map<Long, List<GameBartClick>> allClicks = Map.of(sessionId, clicks);

            // Big5 점수 계산
            Big5BARTCalculationService.BigFiveScores scores = big5BARTCalculationService
                    .calculateBigFiveScores(bartResult, responses, clicks, allResults, allResponses, allClicks);

            // Big5 결과 저장
            big5BARTCalculationService.saveBig5Scores(userId, sessionId, scores);

        } catch (Exception e) {
            log.error("Error calculating and saving Big5 from BART session {}: {}", sessionId, e.getMessage(), e);
        }
    }

}