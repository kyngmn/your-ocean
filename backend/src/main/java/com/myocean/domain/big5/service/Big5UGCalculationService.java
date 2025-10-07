package com.myocean.domain.big5.service;

import com.myocean.domain.big5.entity.Big5Result;
import com.myocean.domain.big5.enums.Big5SourceType;
import com.myocean.domain.big5.repository.Big5ResultRepository;
import com.myocean.domain.ug.entity.GameUgResult;
import com.myocean.domain.ug.entity.GameUgResponse;
import com.myocean.domain.ug.entity.GameUgOrder;
import com.myocean.domain.ug.repository.GameUgResultRepository;
import com.myocean.domain.ug.repository.GameUgResponseRepository;
import com.myocean.domain.ug.repository.GameUgOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class Big5UGCalculationService {

    private final Big5ResultRepository big5ResultRepository;
    private final GameUgResultRepository gameUgResultRepository;
    private final GameUgResponseRepository gameUgResponseRepository;
    private final GameUgOrderRepository gameUgOrderRepository;

    public UGScores calculateUGScores(
            Long sessionId,
            List<GameUgResponse> responses,
            List<GameUgOrder> orders,
            List<GameUgResponse> allResponses,
            List<GameUgOrder> allOrders
    ) {
        // 응답과 주문 매핑
        Map<Long, GameUgOrder> orderMap = orders.stream()
                .collect(Collectors.toMap(GameUgOrder::getId, order -> order));

        // 친화성 계산을 위한 지표들
        AgreeablenessMetrics agreeablenessMetrics = calculateAgreeablenessMetrics(responses, orderMap);
        
        // 외향성 계산을 위한 지표들
        ExtraversionMetrics extraversionMetrics = calculateExtraversionMetrics(responses, orderMap);

        // 전체 데이터에서 정규화를 위한 범위 계산
        NormalizationBounds agreeablenessBounds = calculateAgreeablenessNormalizationBounds(allResponses, allOrders);
        NormalizationBounds extraversionBounds = calculateExtraversionNormalizationBounds(allResponses, allOrders);

        // 친화성 점수 계산 (0-100)
        int agreeableness = calculateAgreeablenessScore(agreeablenessMetrics, agreeablenessBounds);
        
        // 외향성 점수 계산 (0-100)
        int extraversion = calculateExtraversionScore(extraversionMetrics, extraversionBounds);

        return UGScores.builder()
                .agreeableness(agreeableness)
                .extraversion(extraversion)
                .build();
    }

    private AgreeablenessMetrics calculateAgreeablenessMetrics(List<GameUgResponse> responses, Map<Long, GameUgOrder> orderMap) {
        // 제안자 역할 (roleType = 1)의 평균 제안률
        double avgProposalRate = responses.stream()
                .filter(r -> r.getOrderId() != null && orderMap.containsKey(r.getOrderId()))
                .filter(r -> orderMap.get(r.getOrderId()).getRoleType() == 1)
                .mapToInt(GameUgResponse::getProposalRate)
                .average()
                .orElse(0.0);

        // 응답자 역할 (roleType = 2)에서 수락한 제안 중 최소 제안률
        double minAcceptedProposalRate = responses.stream()
                .filter(r -> r.getOrderId() != null && orderMap.containsKey(r.getOrderId()))
                .filter(r -> orderMap.get(r.getOrderId()).getRoleType() == 2)
                .filter(GameUgResponse::getIsAccepted)
                .mapToInt(GameUgResponse::getProposalRate)
                .min()
                .orElse(0);

        // 응답자 역할에서 불공정 제안(3 이하) 수락률
        long totalResponderRounds = responses.stream()
                .filter(r -> r.getOrderId() != null && orderMap.containsKey(r.getOrderId()))
                .filter(r -> orderMap.get(r.getOrderId()).getRoleType() == 2)
                .count();

        long unfairAcceptedCount = responses.stream()
                .filter(r -> r.getOrderId() != null && orderMap.containsKey(r.getOrderId()))
                .filter(r -> orderMap.get(r.getOrderId()).getRoleType() == 2)
                .filter(r -> r.getProposalRate() != null && r.getProposalRate() <= 3)
                .filter(GameUgResponse::getIsAccepted)
                .count();

        double unfairAcceptanceRate = totalResponderRounds > 0 ? (double) unfairAcceptedCount / totalResponderRounds : 0.0;

        return AgreeablenessMetrics.builder()
                .avgProposalRate(avgProposalRate)
                .minAcceptedProposalRate(minAcceptedProposalRate)
                .unfairAcceptanceRate(unfairAcceptanceRate)
                .build();
    }

    private ExtraversionMetrics calculateExtraversionMetrics(List<GameUgResponse> responses, Map<Long, GameUgOrder> orderMap) {
        // 평균 응답 시간 (연속된 응답 간 시간 차이)
        double avgResponseTime = calculateAverageResponseTime(responses);

        // 제안률의 표준편차 (변동성)
        double proposalRateStddev = calculateProposalRateStandardDeviation(responses);

        // 응답자 역할에서의 거절률
        long totalResponderRounds = responses.stream()
                .filter(r -> r.getOrderId() != null && orderMap.containsKey(r.getOrderId()))
                .filter(r -> orderMap.get(r.getOrderId()).getRoleType() == 2)
                .count();

        long rejectedCount = responses.stream()
                .filter(r -> r.getOrderId() != null && orderMap.containsKey(r.getOrderId()))
                .filter(r -> orderMap.get(r.getOrderId()).getRoleType() == 2)
                .filter(r -> !r.getIsAccepted())
                .count();

        double rejectionRate = totalResponderRounds > 0 ? (double) rejectedCount / totalResponderRounds : 0.0;

        return ExtraversionMetrics.builder()
                .avgResponseTime(avgResponseTime)
                .proposalRateStddev(proposalRateStddev)
                .rejectionRate(rejectionRate)
                .build();
    }

    private double calculateAverageResponseTime(List<GameUgResponse> responses) {
        List<GameUgResponse> sortedResponses = responses.stream()
                .filter(r -> r.getFinishedAt() != null)
                .sorted((r1, r2) -> r1.getFinishedAt().compareTo(r2.getFinishedAt()))
                .collect(Collectors.toList());

        if (sortedResponses.size() < 2) return 0.0;

        double totalDiff = 0.0;
        for (int i = 1; i < sortedResponses.size(); i++) {
            LocalDateTime prev = sortedResponses.get(i - 1).getFinishedAt();
            LocalDateTime curr = sortedResponses.get(i).getFinishedAt();
            totalDiff += java.time.Duration.between(prev, curr).toMillis();
        }

        return totalDiff / (sortedResponses.size() - 1);
    }

    private double calculateProposalRateStandardDeviation(List<GameUgResponse> responses) {
        List<Integer> proposalRates = responses.stream()
                .filter(r -> r.getProposalRate() != null)
                .map(GameUgResponse::getProposalRate)
                .collect(Collectors.toList());

        if (proposalRates.size() < 2) return 0.0;

        double mean = proposalRates.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        double variance = proposalRates.stream()
                .mapToDouble(rate -> Math.pow(rate - mean, 2))
                .average()
                .orElse(0.0);

        return Math.sqrt(variance);
    }

    private NormalizationBounds calculateAgreeablenessNormalizationBounds(List<GameUgResponse> allResponses, List<GameUgOrder> allOrders) {
        // 전체 데이터에서 각 지표의 최솟값/최댓값 계산
        Map<Long, GameUgOrder> allOrderMap = allOrders.stream()
                .collect(Collectors.toMap(GameUgOrder::getId, order -> order));

        // 세션별로 그룹화하여 각 세션의 친화성 지표 계산
        Map<Long, List<GameUgResponse>> responsesBySession = allResponses.stream()
                .collect(Collectors.groupingBy(GameUgResponse::getSessionId));

        List<AgreeablenessMetrics> allMetrics = responsesBySession.values().stream()
                .map(sessionResponses -> calculateAgreeablenessMetrics(sessionResponses, allOrderMap))
                .collect(Collectors.toList());

        double minAvgProposalRate = allMetrics.stream().mapToDouble(AgreeablenessMetrics::getAvgProposalRate).min().orElse(0.0);
        double maxAvgProposalRate = allMetrics.stream().mapToDouble(AgreeablenessMetrics::getAvgProposalRate).max().orElse(10.0);
        double minMinAcceptedRate = allMetrics.stream().mapToDouble(AgreeablenessMetrics::getMinAcceptedProposalRate).min().orElse(0.0);
        double maxMinAcceptedRate = allMetrics.stream().mapToDouble(AgreeablenessMetrics::getMinAcceptedProposalRate).max().orElse(10.0);
        double minUnfairAcceptanceRate = allMetrics.stream().mapToDouble(AgreeablenessMetrics::getUnfairAcceptanceRate).min().orElse(0.0);
        double maxUnfairAcceptanceRate = allMetrics.stream().mapToDouble(AgreeablenessMetrics::getUnfairAcceptanceRate).max().orElse(1.0);

        return NormalizationBounds.builder()
                .minAvgProposalRate(minAvgProposalRate)
                .maxAvgProposalRate(maxAvgProposalRate)
                .minMinAcceptedRate(minMinAcceptedRate)
                .maxMinAcceptedRate(maxMinAcceptedRate)
                .minUnfairAcceptanceRate(minUnfairAcceptanceRate)
                .maxUnfairAcceptanceRate(maxUnfairAcceptanceRate)
                .build();
    }

    private NormalizationBounds calculateExtraversionNormalizationBounds(List<GameUgResponse> allResponses, List<GameUgOrder> allOrders) {
        Map<Long, GameUgOrder> allOrderMap = allOrders.stream()
                .collect(Collectors.toMap(GameUgOrder::getId, order -> order));

        Map<Long, List<GameUgResponse>> responsesBySession = allResponses.stream()
                .collect(Collectors.groupingBy(GameUgResponse::getSessionId));

        List<ExtraversionMetrics> allMetrics = responsesBySession.values().stream()
                .map(sessionResponses -> calculateExtraversionMetrics(sessionResponses, allOrderMap))
                .collect(Collectors.toList());

        double minAvgResponseTime = allMetrics.stream().mapToDouble(ExtraversionMetrics::getAvgResponseTime).min().orElse(0.0);
        double maxAvgResponseTime = allMetrics.stream().mapToDouble(ExtraversionMetrics::getAvgResponseTime).max().orElse(10000.0);
        double minProposalRateStddev = allMetrics.stream().mapToDouble(ExtraversionMetrics::getProposalRateStddev).min().orElse(0.0);
        double maxProposalRateStddev = allMetrics.stream().mapToDouble(ExtraversionMetrics::getProposalRateStddev).max().orElse(5.0);
        double minRejectionRate = allMetrics.stream().mapToDouble(ExtraversionMetrics::getRejectionRate).min().orElse(0.0);
        double maxRejectionRate = allMetrics.stream().mapToDouble(ExtraversionMetrics::getRejectionRate).max().orElse(1.0);

        return NormalizationBounds.builder()
                .minAvgResponseTime(minAvgResponseTime)
                .maxAvgResponseTime(maxAvgResponseTime)
                .minProposalRateStddev(minProposalRateStddev)
                .maxProposalRateStddev(maxProposalRateStddev)
                .minRejectionRate(minRejectionRate)
                .maxRejectionRate(maxRejectionRate)
                .build();
    }

    private double normalize(double value, double min, double max) {
        if (max - min == 0) return 0.0;
        return Math.max(0.0, Math.min(1.0, (value - min) / (max - min)));
    }

    private int calculateAgreeablenessScore(AgreeablenessMetrics metrics, NormalizationBounds bounds) {
        // 각 지표를 0-1로 정규화
        double normAvgProposalRate = normalize(metrics.getAvgProposalRate(), bounds.getMinAvgProposalRate(), bounds.getMaxAvgProposalRate());
        double normMinAcceptedRate = normalize(metrics.getMinAcceptedProposalRate(), bounds.getMinMinAcceptedRate(), bounds.getMaxMinAcceptedRate());
        double normUnfairAcceptanceRate = normalize(metrics.getUnfairAcceptanceRate(), bounds.getMinUnfairAcceptanceRate(), bounds.getMaxUnfairAcceptanceRate());

        // 가중평균으로 친화성 점수 계산 (0-100)
        double score = normAvgProposalRate * 0.4 +
                      normMinAcceptedRate * 0.4 +
                      normUnfairAcceptanceRate * 0.2;

        return Math.max(0, Math.min(100, (int) Math.round(score * 100)));
    }

    private int calculateExtraversionScore(ExtraversionMetrics metrics, NormalizationBounds bounds) {
        // 각 지표를 0-1로 정규화
        double normAvgResponseTime = normalize(metrics.getAvgResponseTime(), bounds.getMinAvgResponseTime(), bounds.getMaxAvgResponseTime());
        double normProposalRateStddev = normalize(metrics.getProposalRateStddev(), bounds.getMinProposalRateStddev(), bounds.getMaxProposalRateStddev());
        double normRejectionRate = normalize(metrics.getRejectionRate(), bounds.getMinRejectionRate(), bounds.getMaxRejectionRate());

        // 가중평균으로 외향성 점수 계산 (응답 시간은 빠를수록 높음 - 역변환)
        double score = (1 - normAvgResponseTime) * 0.4 +  // 빠를수록 점수 높음
                      normProposalRateStddev * 0.3 +
                      normRejectionRate * 0.3;

        return Math.max(0, Math.min(100, (int) Math.round(score * 100)));
    }

    public void calculateAndSaveBig5Scores(Integer userId, Long sessionId) {
        try {
            // Big5 계산에 필요한 데이터 조회
            List<GameUgResponse> responses = gameUgResponseRepository.findBySessionId(sessionId);
            List<GameUgOrder> orders = gameUgOrderRepository.findAll();
            
            // 정규화를 위한 전체 데이터 조회
            List<GameUgResponse> allResponses = gameUgResponseRepository.findAll();
            List<GameUgOrder> allOrders = gameUgOrderRepository.findAll();

            // Big5 점수 계산
            UGScores scores = calculateUGScores(sessionId, responses, orders, allResponses, allOrders);

            // Big5 결과 저장
            saveBig5Scores(userId, sessionId, scores);

        } catch (Exception e) {
            log.error("Error calculating and saving UG Big5 for session {}: {}", sessionId, e.getMessage(), e);
        }
    }

    public void saveBig5Scores(Integer userId, Long sessionId, UGScores scores) {
        try {
            // 이미 해당 UG 세션에 대한 Big5 결과가 있는지 확인
            List<Big5Result> existingResults = big5ResultRepository
                    .findByUserIdAndSourceType(userId, Big5SourceType.GAME);
            
            boolean alreadyExists = existingResults.stream()
                    .anyMatch(r -> r.getSourceId().equals(sessionId));
            
            if (!alreadyExists) {
                Big5Result big5Result = Big5Result.builder()
                        .userId(userId)
                        .sourceType(Big5SourceType.GAME)
                        .sourceId(sessionId)
                        .resultE(scores.getExtraversion())
                        .resultA(scores.getAgreeableness())
                        .resultO(null) // UG에서는 개방성 계산 안함 - null
                        .resultC(null) // UG에서는 성실성 계산 안함 - null
                        .resultN(null) // UG에서는 신경성 계산 안함 - null
                        .build();

                big5ResultRepository.save(big5Result);
                log.info("UG Big5 scores saved for session: {}", sessionId);
            } else {
                log.info("UG Big5 result already exists for session: {}", sessionId);
            }
        } catch (Exception e) {
            log.error("Error saving UG Big5 result for session {}: {}", sessionId, e.getMessage(), e);
        }
    }

    // 내부 클래스들
    @lombok.Builder
    @lombok.Getter
    public static class UGScores {
        private final int agreeableness;
        private final int extraversion;
    }

    @lombok.Builder
    @lombok.Getter
    private static class AgreeablenessMetrics {
        private final double avgProposalRate;
        private final double minAcceptedProposalRate;
        private final double unfairAcceptanceRate;
    }

    @lombok.Builder
    @lombok.Getter
    private static class ExtraversionMetrics {
        private final double avgResponseTime;
        private final double proposalRateStddev;
        private final double rejectionRate;
    }

    @lombok.Builder
    @lombok.Getter
    private static class NormalizationBounds {
        private final double minAvgProposalRate;
        private final double maxAvgProposalRate;
        private final double minMinAcceptedRate;
        private final double maxMinAcceptedRate;
        private final double minUnfairAcceptanceRate;
        private final double maxUnfairAcceptanceRate;
        private final double minAvgResponseTime;
        private final double maxAvgResponseTime;
        private final double minProposalRateStddev;
        private final double maxProposalRateStddev;
        private final double minRejectionRate;
        private final double maxRejectionRate;
    }
}