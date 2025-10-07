package com.myocean.domain.ug.service.impl;

import com.myocean.domain.gamesession.entity.GameSession;
import com.myocean.domain.gamesession.enums.GameType;
import com.myocean.domain.gamesession.repository.GameSessionRepository;
import com.myocean.domain.ug.dto.request.GameUgResponseRequest;
import com.myocean.domain.ug.dto.response.GameUgOrderResponse;
import com.myocean.domain.ug.dto.response.GameUgResponseDto;
import com.myocean.domain.ug.entity.GameUgOrder;
import com.myocean.domain.ug.entity.GameUgResponse;
import com.myocean.domain.ug.entity.GameUgResult;
import com.myocean.domain.ug.enums.PersonaType;
import com.myocean.domain.ug.repository.GameUgOrderRepository;
import com.myocean.domain.ug.repository.GameUgResponseRepository;
import com.myocean.domain.ug.repository.GameUgResultRepository;
import com.myocean.domain.ug.service.GameUgService;
import com.myocean.domain.big5.service.Big5UGCalculationService;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GameUgServiceImpl implements GameUgService {

    private final GameUgOrderRepository gameUgOrderRepository;
    private final GameUgResponseRepository gameUgResponseRepository;
    private final GameUgResultRepository gameUgResultRepository;
    private final GameSessionRepository gameSessionRepository;
    private final Big5UGCalculationService big5UGCalculationService;

    @Override
    public List<GameUgOrderResponse> getGameOrders() {
        log.info("Fetching all UG game orders");

        List<GameUgOrder> orders = gameUgOrderRepository.findAll();

        return orders.stream()
                .map(order -> GameUgOrderResponse.from(
                        order.getId(),
                        order.getRoleType(),
                        order.getPersonaType(),
                        order.getMoney(),
                        order.getRate()
                ))
                .toList();
    }

    @Override
    public List<GameUgOrderResponse> getGameOrdersByDay(Integer day) {
        log.info("Fetching UG game orders for day: {}", day);

        // Day별 ID 범위 계산
        long startId = (day - 1) * 30L + 1;
        long endId = day * 30L;

        List<GameUgOrder> orders = gameUgOrderRepository.findByIdRange(startId, endId);

        log.info("Found {} orders for day {} (ID range: {}-{})", orders.size(), day, startId, endId);

        return orders.stream()
                .map(order -> GameUgOrderResponse.from(
                        order.getId(),
                        order.getRoleType(),
                        order.getPersonaType(),
                        order.getMoney(),
                        order.getRate()
                ))
                .toList();
    }

    @Override
    @Transactional
    public GameUgResponseDto submitGameResponse(Integer userId, Long sessionId, Integer roundId, GameUgResponseRequest request) {
        log.info("Submitting UG game response for user: {}, session: {}, round: {}", userId, sessionId, roundId);

        // 1. 게임 세션 검증
        GameSession gameSession = gameSessionRepository
                .findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.GAME_SESSION_NOT_FOUND));

        if (gameSession.getFinishedAt() != null) {
            throw new GeneralException(ErrorStatus.GAME_SESSION_ALREADY_FINISHED);
        }

        // 2. 중복 라운드 제출 방지
        boolean roundExists = gameUgResponseRepository.existsBySessionIdAndRound(sessionId, roundId);
        if (roundExists) {
            throw new GeneralException(ErrorStatus.UG_ORDER_NOT_FOUND); // 중복 라운드 제출
        }

        // 2. Order ID 자동 계산 (요청에 없을 경우)
        Long orderId = request.orderId();
        if (orderId == null) {
            Integer gameDay = calculateGameDayFromSession(sessionId);
            orderId = calculateOrderIdFromDayAndRound(gameDay, roundId);
            log.info("Auto-calculated orderId: {} (gameDay: {}, round: {})", orderId, gameDay, roundId);
        }

        // 3. 오더 검증 및 정보 조회
        GameUgOrder order = gameUgOrderRepository.findById(orderId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.UG_ORDER_NOT_FOUND));

        // 3. Role별 로직 처리 및 입력값 검증
        int proposalAmount = 0;
        boolean finalAccepted;
        Integer finalProposalRate;
        int roleType = order.getRoleType();

        switch (roleType) {
            case 1 -> { // 사용자가 제안자, 시스템이 수락/거절 결정
                if (request.proposalRate() == null) {
                    throw new GeneralException(ErrorStatus.UG_ORDER_NOT_FOUND); // proposalRate 필수
                }
                proposalAmount = calculateProposalAmount(request.proposalRate(), order.getMoney(), request.totalAmount());
                finalAccepted = shouldSystemAcceptProposal(request.proposalRate(), order.getPersonaType(), order.getMoney());
                finalProposalRate = request.proposalRate();
                log.info("Role 1 - User proposes to take {}%, System decision: {}",
                        request.proposalRate() * 10, finalAccepted ? "ACCEPTED" : "REJECTED");
            }
            case 2 -> { // 시스템이 제안자, 사용자가 수락/거절
                if (request.isAccepted() == null) {
                    throw new GeneralException(ErrorStatus.UG_ORDER_NOT_FOUND); // isAccepted 필수
                }
                proposalAmount = calculateProposalAmount(order.getRate(), order.getMoney(), request.totalAmount());
                finalAccepted = request.isAccepted();
                finalProposalRate = order.getRate();
                log.info("Role 2 - System offers {}% to user, User decision: {}",
                        order.getRate() * 10, finalAccepted ? "ACCEPTED" : "REJECTED");
            }
            case 3 -> { // 사용자가 제안자, 시스템이 무조건 수락
                if (request.proposalRate() == null) {
                    throw new GeneralException(ErrorStatus.UG_ORDER_NOT_FOUND); // proposalRate 필수
                }
                proposalAmount = calculateProposalAmount(request.proposalRate(), order.getMoney(), request.totalAmount());
                finalAccepted = true; // 무조건 수락
                finalProposalRate = request.proposalRate();
                log.info("Role 3 - User proposes to take {}%, System automatically accepts",
                        request.proposalRate() * 10);
            }
            default -> throw new GeneralException(ErrorStatus.UG_ORDER_NOT_FOUND);
        }

        // 4. 응답 저장
        GameUgResponse ugResponse = GameUgResponse.builder()
                .sessionId(sessionId)
                .round(roundId)
                .orderId(orderId) // 자동 계산된 orderId 사용
                .money(proposalAmount)
                .isAccepted(finalAccepted)
                .proposalRate(finalProposalRate)
                .finishedAt(LocalDateTime.now())
                .build();

        GameUgResponse savedResponse = gameUgResponseRepository.save(ugResponse);

        log.info("UG game response saved with ID: {}", savedResponse.getId());

        // 4. 30라운드 완료 체크 (응답 저장 후)
        boolean isCompleted = isSessionCompleted(sessionId);
        
        if (isCompleted) {
            log.info("Session {} completed 30 rounds. Auto calculating UG results.", sessionId);
            
            // 30라운드 완료 시 자동으로 UG 결과 계산 및 저장
            autoCalculateUgResult(sessionId);
        }

        return new GameUgResponseDto(
                savedResponse.getId(),
                savedResponse.getSessionId(),
                savedResponse.getRound(),
                savedResponse.getOrderId(),
                savedResponse.getMoney(),
                savedResponse.getIsAccepted(),
                savedResponse.getProposalRate(),
                savedResponse.getFinishedAt(),
                isCompleted,
                null // gameResult는 별도 조회를 통해 확인
        );
    }



    @Override
    public List<GameUgOrderResponse> getGameOrdersBySession(Long sessionId) {
        log.info("Fetching UG game orders for. session: {}", sessionId);

        // 세션으로부터 Game Day 계산
        Integer gameDay = calculateGameDayFromSession(sessionId);

        // Day별 Order 조회
        return getGameOrdersByDay(gameDay);
    }

    private boolean isSessionCompleted(Long sessionId) {
        long responseCount = gameUgResponseRepository.countBySessionId(sessionId);
        log.info("Session {} has {} responses", sessionId, responseCount);
        return responseCount == 30; // 정확히 30개여야 함
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public GameUgResult calculateAndSaveResult(Long sessionId) {
        log.info("=== [NEW TX] calculateAndSaveResult sessionId={} thread={} txName={} ===",
            sessionId, Thread.currentThread().getName(),
            TransactionSynchronizationManager.getCurrentTransactionName());

        // 세션 존재 확인 (finishedAt 체크 제거)
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.GAME_SESSION_NOT_FOUND));

        // 멱등성 확인
        Optional<GameUgResult> existing = gameUgResultRepository.findBySessionId(sessionId);
        if (existing.isPresent()) {
            log.info("Result already exists for session {}", sessionId);
            return existing.get();
        }

        // 실제 게임 응답 결과를 기반으로 총 획득 금액 계산
        List<GameUgResponse> responses = gameUgResponseRepository.findBySessionId(sessionId);
        int totalEarnedAmount = 0;

        log.info("Calculating UG result for session {} with {} responses", sessionId, responses.size());

        for (GameUgResponse response : responses) {
            GameUgOrder order = gameUgOrderRepository.findById(response.getOrderId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.UG_ORDER_NOT_FOUND));
            
            int earnedFromRound = calculateEarnedAmount(response, order);
            totalEarnedAmount += earnedFromRound;
            
            log.debug("Round {}: orderId={}, earned={}, total={}", 
                    response.getRound(), response.getOrderId(), earnedFromRound, totalEarnedAmount);
        }

        // ID를 명시적으로 설정하고 GameSession 관계는 읽기 전용으로 설정
        GameUgResult result = new GameUgResult();
        result.setSessionId(sessionId); // 명시적 ID 설정
        result.setEarnedAmount(totalEarnedAmount);
        result.setFinishedAt(LocalDateTime.now());

        try {
            GameUgResult saved = gameUgResultRepository.save(result);
            gameUgResultRepository.flush();
            log.info("UG result saved OK: sessionId={}, totalEarned={}", saved.getSessionId(), saved.getEarnedAmount());
            
            // UG 결과 저장 후 Big5 지표 계산 및 저장
            big5UGCalculationService.calculateAndSaveBig5Scores(session.getUserId(), sessionId);
            
            return saved;
        } catch (Exception e) {
            log.error("Error on save (sessionId={}): {}", sessionId, e.getMessage(), e);
            // 모든 예외에 대해 이미 저장된 결과가 있는지 확인
            Optional<GameUgResult> fallbackResult = gameUgResultRepository.findBySessionId(sessionId);
            if (fallbackResult.isPresent()) {
                log.info("Result already exists for session {} - returning existing result", sessionId);
                return fallbackResult.get();
            }
            // 여러 시도로도 실패하면 기본 결과 생성
            log.warn("Creating default result for session {} due to persistent errors", sessionId);
            GameUgResult defaultResult = new GameUgResult();
            defaultResult.setSessionId(sessionId);
            defaultResult.setEarnedAmount(0); // 기본값
            defaultResult.setFinishedAt(LocalDateTime.now());
            return gameUgResultRepository.save(defaultResult);
        }
    }

    private int calculateEarnedAmount(GameUgResponse response, GameUgOrder order) {
        int roleType = order.getRoleType();
        boolean isAccepted = response.getIsAccepted();
        int proposalAmount = response.getMoney(); // 이미 계산된 제안 금액

        if (!isAccepted) {
            return 0; // 거절하면 0원
        }

        return switch (roleType) {
            case 1, 3 -> { // 사용자가 제안자: 자신이 선택한 비율만큼 획득
                yield proposalAmount; // proposalRate에 해당하는 금액을 사용자가 가져감
            }
            case 2 -> { // 사용자가 응답자 (시스템 제안): 시스템이 제안한 금액을 획득
                yield proposalAmount; // 시스템이 제안한 금액을 사용자가 가져감
            }
            default -> {
                log.warn("Unknown role type: {}", roleType);
                yield 0;
            }
        };
    }

    private int getTotalAmount(com.myocean.domain.ug.enums.MoneySize moneySize) {
        // 결과 계산 시 범위의 중간값 사용 (실제로는 프론트에서 랜덤 생성)
        return switch (moneySize) {
            case SMALL -> 55_000;     // 1만~10만원 중간값
            case LARGE -> 550_000_000; // 1억~10억원 중간값
        };
    }

    @Transactional
    private void updateGameSessionStatus(Long sessionId) {
        GameSession gameSession = gameSessionRepository.findById(sessionId)
                .orElse(null);
        if (gameSession != null && gameSession.getFinishedAt() == null) {
            try {
                gameSession.finish();
                gameSessionRepository.save(gameSession);
                log.info("Game session {} marked as finished", sessionId);
            } catch (IllegalStateException e) {
                log.warn("Game session {} already finished", sessionId);
            }
        }
    }

    /**
     * Role 1에서 시스템이 사용자의 제안을 수락/거절할지 결정하는 알고리즘
     * 사용자가 자신이 가져갈 비율을 제안하므로, 낮은 비율(겸손한 제안)일수록 수락 가능성이 높음
     *
     * @param proposalRate 사용자가 제안한 비율 (1=10%, 2=20%, ...)
     * @param personaType 상대방 페르소나 타입
     * @param moneySize 금액 크기
     * @return true면 수락, false면 거절
     */
    private boolean shouldSystemAcceptProposal(int proposalRate, PersonaType personaType, com.myocean.domain.ug.enums.MoneySize moneySize) {
        int proposalPercentage = proposalRate * 10; // 1 -> 10%, 2 -> 20%

        // 기본 거절 임계값 설정 (사용자가 이 비율 이상 가져가려 하면 거절)
        int baseRejectThreshold = switch (personaType) {
            case FAMILY -> 70;    // 가족: 관대함, 70% 이상 가져가려 하면 거절
            case FRIEND -> 60;    // 친구: 보통, 60% 이상 가져가려 하면 거절
            case STRANGER -> 50;  // 낯선사람: 까다로움, 50% 이상 가져가려 하면 거절
        };

        // 금액 크기에 따른 조정
        int adjustedThreshold = switch (moneySize) {
            case SMALL -> baseRejectThreshold + 10; // 소액일 때 더 관대 (+10%)
            case LARGE -> baseRejectThreshold - 10; // 고액일 때 더 까다로움 (-10%)
        };

        // 확률적 요소 추가 (완전 예측 가능하지 않도록)
        double randomFactor = Math.random() * 10 - 5; // -5% ~ +5% 랜덤
        double finalThreshold = adjustedThreshold + randomFactor;

        boolean accepted = proposalPercentage < finalThreshold;

        log.info("Proposal decision: user wants {}%, threshold {}% -> {}",
                proposalPercentage, Math.round(finalThreshold), accepted ? "ACCEPTED" : "REJECTED");

        return accepted;
    }

    /**
     * 비율을 기반으로 실제 제안 금액을 계산
     */
    private int calculateProposalAmount(Integer proposalRate, com.myocean.domain.ug.enums.MoneySize moneySize, int totalAmount) {
        if (proposalRate == null) {
            return 0;
        }
        int proposalPercentage = proposalRate * 10; // 1 -> 10%
        return (totalAmount * proposalPercentage) / 100;
    }

    /**
     * 사용자의 UG 게임 완료 횟수에 따른 Day 계산
     * 0회 → Day 1, 1회 → Day 2, 2회 → Day 3, 3회 → Day 1 (반복)
     */
    private Integer calculateGameDayForUser(Integer userId) {
        Long completedCount = gameSessionRepository.countCompletedGamesByUserIdAndGameType(userId, GameType.UG);
        Integer gameDay = (int) (completedCount % 3) + 1;
        log.info("User {} has completed {} UG games, assigned to Day {}", userId, completedCount, gameDay);
        return gameDay;
    }

    /**
     * 세션 ID로부터 해당 세션의 Game Day 계산
     */
    private Integer calculateGameDayFromSession(Long sessionId) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.GAME_SESSION_NOT_FOUND));
        return calculateGameDayForUser(session.getUserId());
    }

    /**
     * Game Day와 라운드를 기반으로 Order ID 계산
     */
    private Long calculateOrderIdFromDayAndRound(Integer gameDay, Integer roundId) {
        return (gameDay - 1) * 30L + roundId;
    }

    /**
     * 30라운드 완료 시 UG 결과 계산 및 저장을 자동으로 처리
     */
    private void autoCalculateUgResult(Long sessionId) {
        try {
            log.info("Auto calculating and saving UG result for session: {}", sessionId);
            
            // 30라운드 완료 시 UG 결과 계산 및 저장만 수행 (finishedAt 업데이트 안함)
            calculateAndSaveResult(sessionId);
            
        } catch (Exception e) {
            log.error("Failed to calculate and save UG result for session {}: {}", sessionId, e.getMessage(), e);
            // 에러가 발생해도 게임 진행에는 영향을 주지 않도록 함
        }
    }
}