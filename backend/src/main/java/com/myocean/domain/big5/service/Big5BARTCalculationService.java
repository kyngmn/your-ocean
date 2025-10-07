package com.myocean.domain.big5.service;

import com.myocean.domain.bart.entity.GameBartResult;
import com.myocean.domain.bart.entity.GameBartResponse;
import com.myocean.domain.bart.entity.GameBartClick;
import com.myocean.domain.big5.entity.Big5Result;
import com.myocean.domain.big5.enums.Big5SourceType;
import com.myocean.domain.big5.repository.Big5ResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class Big5BARTCalculationService {

    private final Big5ResultRepository big5ResultRepository;

    public BigFiveScores calculateBigFiveScores(
            GameBartResult result,
            List<GameBartResponse> responses,
            List<GameBartClick> clicks,
            List<GameBartResult> allResults,
            Map<Long, List<GameBartResponse>> allResponses,
            Map<Long, List<GameBartClick>> allClicks
    ) {

        // 클릭 데이터 통계 계산
        double avgClickInterval = calculateAvgClickInterval(clicks);
        double stdClickInterval = calculateStdClickInterval(clicks);
        double postFailDelay = calculatePostFailDelay(responses, clicks);
        double stdPumping = calculateStdPumping(responses);

        // 전체 데이터에서 정규화를 위한 min/max 값 계산
        NormalizationBounds bounds = calculateNormalizationBounds(allResults, allResponses, allClicks);

        // 정규화된 값들 계산
        double normAvgPumps = normalize(result.getAvgPumps().doubleValue(), bounds.minAvgPumps, bounds.maxAvgPumps);
        double normAvgClickInterval = normalize(avgClickInterval, bounds.minAvgClickInterval, bounds.maxAvgClickInterval);
        double normPostFailDelay = normalize(postFailDelay, bounds.minPostFailDelay, bounds.maxPostFailDelay);
        double normPumpingStd = normalize(stdPumping, bounds.minPumpingStd, bounds.maxPumpingStd);
        double normClickIntervalStd = normalize(stdClickInterval, bounds.minClickIntervalStd, bounds.maxClickIntervalStd);

        // 성공률 및 실패율 계산
        double successRate = result.getSuccessBalloons().doubleValue() / result.getTotalBalloons().doubleValue();
        double failRate = result.getFailBalloons().doubleValue() / result.getTotalBalloons().doubleValue();
        double missedRate = result.getMissedReward().doubleValue() /
                          (result.getRewardAmount().doubleValue() + result.getMissedReward().doubleValue());

        // Big Five 점수 계산
        int extraversion = calculateExtraversion(normAvgPumps, successRate, normAvgClickInterval);
        int neuroticism = calculateNeuroticism(failRate, missedRate, normPostFailDelay);
        int openness = calculateOpenness(normPumpingStd, normClickIntervalStd);

        return BigFiveScores.builder()
                .extraversion(extraversion)
                .neuroticism(neuroticism)
                .openness(openness)
                .build();
    }

    public static double calculateAvgClickInterval(List<GameBartClick> clicks) {
        if (clicks.size() < 2) return 0.0;

        List<LocalDateTime> timestamps = clicks.stream()
                .map(GameBartClick::getClickedAt)
                .sorted()
                .collect(Collectors.toList());

        double sum = 0.0;
        for (int i = 1; i < timestamps.size(); i++) {
            sum += Duration.between(timestamps.get(i - 1), timestamps.get(i)).toMillis();
        }
        return sum / (timestamps.size() - 1);
    }

    public static double calculateStdClickInterval(List<GameBartClick> clicks) {
        if (clicks.size() < 2) return 0.0;

        List<LocalDateTime> timestamps = clicks.stream()
                .map(GameBartClick::getClickedAt)
                .sorted()
                .collect(Collectors.toList());

        List<Long> intervals = new ArrayList<>();
        for (int i = 1; i < timestamps.size(); i++) {
            intervals.add(Duration.between(timestamps.get(i - 1), timestamps.get(i)).toMillis());
        }

        double mean = intervals.stream().mapToLong(x -> x).average().orElse(0.0);
        double variance = intervals.stream().mapToDouble(x -> Math.pow(x - mean, 2)).average().orElse(0.0);
        return Math.sqrt(variance);
    }

    public static double calculateStdPumping(List<GameBartResponse> responses) {
        if (responses.size() < 2) return 0.0;

        List<Integer> pumpingCounts = responses.stream()
                .map(GameBartResponse::getPumpingCnt)
                .filter(cnt -> cnt != null)
                .collect(Collectors.toList());

        if (pumpingCounts.size() < 2) return 0.0;

        double mean = pumpingCounts.stream().mapToInt(x -> x).average().orElse(0.0);
        double variance = pumpingCounts.stream().mapToDouble(x -> Math.pow(x - mean, 2)).average().orElse(0.0);
        return Math.sqrt(variance);
    }

    public static double calculatePostFailDelay(List<GameBartResponse> responses, List<GameBartClick> clicks) {
        // 실패 후 다음 라운드 첫 클릭까지의 지연시간 계산
        List<GameBartResponse> failedResponses = responses.stream()
                .filter(r -> r.getIsPopped())
                .collect(Collectors.toList());

        if (failedResponses.isEmpty()) return 0.0;

        double totalDelay = 0.0;
        int delayCount = 0;

        for (GameBartResponse failedResponse : failedResponses) {
            // 다음 라운드가 있는지 확인
            GameBartResponse nextResponse = responses.stream()
                    .filter(r -> r.getRoundIndex() == failedResponse.getRoundIndex() + 1)
                    .findFirst()
                    .orElse(null);

            if (nextResponse != null) {
                // 다음 라운드의 첫 클릭 찾기
                GameBartClick firstNextClick = clicks.stream()
                        .filter(c -> c.getResponseId().equals(nextResponse.getId()))
                        .min((c1, c2) -> c1.getClickIndex().compareTo(c2.getClickIndex()))
                        .orElse(null);

                if (firstNextClick != null && failedResponse.getFinishedAt() != null) {
                    long delay = Duration.between(failedResponse.getFinishedAt(), firstNextClick.getClickedAt()).toMillis();
                    totalDelay += delay;
                    delayCount++;
                }
            }
        }

        return delayCount > 0 ? totalDelay / delayCount : 0.0;
    }

    public static double normalize(double value, double min, double max) {
        if (max - min == 0) return 0.0;
        return Math.max(0.0, Math.min(1.0, (value - min) / (max - min)));
    }

    public static int calculateExtraversion(
            double normAvgPumps,
            double successRate,
            double normAvgClickInterval
    ) {
        double score = 70 * normAvgPumps
                     + 20 * successRate
                     + 10 * (1 - normAvgClickInterval);
        return Math.max(0, Math.min(100, (int) Math.round(score)));
    }

    public static int calculateNeuroticism(
            double failRate,
            double missedRate,
            double normPostFailDelay
    ) {
        double score = 40 * failRate
                     + 30 * missedRate
                     + 30 * normPostFailDelay;
        return Math.max(0, Math.min(100, (int) Math.round(score)));
    }

    public static int calculateOpenness(
            double normPumpingStd,
            double normClickIntervalStd
    ) {
        double score = 50 * normPumpingStd
                     + 50 * normClickIntervalStd;
        return Math.max(0, Math.min(100, (int) Math.round(score)));
    }

    private NormalizationBounds calculateNormalizationBounds(
            List<GameBartResult> allResults,
            Map<Long, List<GameBartResponse>> allResponses,
            Map<Long, List<GameBartClick>> allClicks
    ) {

        double minAvgPumps = allResults.stream()
                .mapToDouble(r -> r.getAvgPumps().doubleValue())
                .min().orElse(0.0);
        double maxAvgPumps = allResults.stream()
                .mapToDouble(r -> r.getAvgPumps().doubleValue())
                .max().orElse(1.0);

        List<Double> allAvgClickIntervals = new ArrayList<>();
        List<Double> allPostFailDelays = new ArrayList<>();
        List<Double> allPumpingStds = new ArrayList<>();
        List<Double> allClickIntervalStds = new ArrayList<>();

        for (Map.Entry<Long, List<GameBartClick>> entry : allClicks.entrySet()) {
            Long sessionId = entry.getKey();
            List<GameBartClick> clicks = entry.getValue();
            List<GameBartResponse> responses = allResponses.get(sessionId);

            if (responses != null && !clicks.isEmpty()) {
                allAvgClickIntervals.add(calculateAvgClickInterval(clicks));
                allPostFailDelays.add(calculatePostFailDelay(responses, clicks));
                allPumpingStds.add(calculateStdPumping(responses));
                allClickIntervalStds.add(calculateStdClickInterval(clicks));
            }
        }

        return NormalizationBounds.builder()
                .minAvgPumps(minAvgPumps)
                .maxAvgPumps(maxAvgPumps)
                .minAvgClickInterval(allAvgClickIntervals.stream().mapToDouble(d -> d).min().orElse(0.0))
                .maxAvgClickInterval(allAvgClickIntervals.stream().mapToDouble(d -> d).max().orElse(1.0))
                .minPostFailDelay(allPostFailDelays.stream().mapToDouble(d -> d).min().orElse(0.0))
                .maxPostFailDelay(allPostFailDelays.stream().mapToDouble(d -> d).max().orElse(1.0))
                .minPumpingStd(allPumpingStds.stream().mapToDouble(d -> d).min().orElse(0.0))
                .maxPumpingStd(allPumpingStds.stream().mapToDouble(d -> d).max().orElse(1.0))
                .minClickIntervalStd(allClickIntervalStds.stream().mapToDouble(d -> d).min().orElse(0.0))
                .maxClickIntervalStd(allClickIntervalStds.stream().mapToDouble(d -> d).max().orElse(1.0))
                .build();
    }

    public void saveBig5Scores(Integer userId, Long sessionId, BigFiveScores scores) {
        try {
            // 이미 해당 BART 세션에 대한 Big5 결과가 있는지 확인
            List<Big5Result> existingResults = big5ResultRepository
                    .findByUserIdAndSourceType(userId, Big5SourceType.GAME_SESSION);
            
            boolean alreadyExists = existingResults.stream()
                    .anyMatch(r -> r.getSourceId().equals(sessionId));
            
            if (!alreadyExists) {
                Big5Result big5Result = Big5Result.builder()
                        .userId(userId)
                        .sourceType(Big5SourceType.GAME_SESSION)
                        .sourceId(sessionId)
                        .resultE(scores.getExtraversion())
                        .resultN(scores.getNeuroticism())
                        .resultO(scores.getOpenness())
                        .resultC(null) // BART에서는 성실성 계산 안함 - null
                        .resultA(null) // BART에서는 친화성 계산 안함 - null
                        .build();

                big5ResultRepository.save(big5Result);
                log.info("Big5 scores saved for BART session: {}", sessionId);
            } else {
                log.info("Big5 result already exists for BART session: {}", sessionId);
            }
        } catch (Exception e) {
            log.error("Error saving Big5 result for session {}: {}", sessionId, e.getMessage(), e);
        }
    }

    public static class BigFiveScores {
        private final int extraversion;
        private final int neuroticism;
        private final int openness;

        private BigFiveScores(Builder builder) {
            this.extraversion = builder.extraversion;
            this.neuroticism = builder.neuroticism;
            this.openness = builder.openness;
        }

        public static Builder builder() {
            return new Builder();
        }

        public int getExtraversion() {
            return extraversion;
        }

        public int getNeuroticism() {
            return neuroticism;
        }

        public int getOpenness() {
            return openness;
        }

        public static class Builder {
            private int extraversion;
            private int neuroticism;
            private int openness;

            public Builder extraversion(int extraversion) {
                this.extraversion = extraversion;
                return this;
            }

            public Builder neuroticism(int neuroticism) {
                this.neuroticism = neuroticism;
                return this;
            }

            public Builder openness(int openness) {
                this.openness = openness;
                return this;
            }

            public BigFiveScores build() {
                return new BigFiveScores(this);
            }
        }
    }

    private static class NormalizationBounds {
        private final double minAvgPumps;
        private final double maxAvgPumps;
        private final double minAvgClickInterval;
        private final double maxAvgClickInterval;
        private final double minPostFailDelay;
        private final double maxPostFailDelay;
        private final double minPumpingStd;
        private final double maxPumpingStd;
        private final double minClickIntervalStd;
        private final double maxClickIntervalStd;

        private NormalizationBounds(Builder builder) {
            this.minAvgPumps = builder.minAvgPumps;
            this.maxAvgPumps = builder.maxAvgPumps;
            this.minAvgClickInterval = builder.minAvgClickInterval;
            this.maxAvgClickInterval = builder.maxAvgClickInterval;
            this.minPostFailDelay = builder.minPostFailDelay;
            this.maxPostFailDelay = builder.maxPostFailDelay;
            this.minPumpingStd = builder.minPumpingStd;
            this.maxPumpingStd = builder.maxPumpingStd;
            this.minClickIntervalStd = builder.minClickIntervalStd;
            this.maxClickIntervalStd = builder.maxClickIntervalStd;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private double minAvgPumps;
            private double maxAvgPumps;
            private double minAvgClickInterval;
            private double maxAvgClickInterval;
            private double minPostFailDelay;
            private double maxPostFailDelay;
            private double minPumpingStd;
            private double maxPumpingStd;
            private double minClickIntervalStd;
            private double maxClickIntervalStd;

            public Builder minAvgPumps(double minAvgPumps) {
                this.minAvgPumps = minAvgPumps;
                return this;
            }

            public Builder maxAvgPumps(double maxAvgPumps) {
                this.maxAvgPumps = maxAvgPumps;
                return this;
            }

            public Builder minAvgClickInterval(double minAvgClickInterval) {
                this.minAvgClickInterval = minAvgClickInterval;
                return this;
            }

            public Builder maxAvgClickInterval(double maxAvgClickInterval) {
                this.maxAvgClickInterval = maxAvgClickInterval;
                return this;
            }

            public Builder minPostFailDelay(double minPostFailDelay) {
                this.minPostFailDelay = minPostFailDelay;
                return this;
            }

            public Builder maxPostFailDelay(double maxPostFailDelay) {
                this.maxPostFailDelay = maxPostFailDelay;
                return this;
            }

            public Builder minPumpingStd(double minPumpingStd) {
                this.minPumpingStd = minPumpingStd;
                return this;
            }

            public Builder maxPumpingStd(double maxPumpingStd) {
                this.maxPumpingStd = maxPumpingStd;
                return this;
            }

            public Builder minClickIntervalStd(double minClickIntervalStd) {
                this.minClickIntervalStd = minClickIntervalStd;
                return this;
            }

            public Builder maxClickIntervalStd(double maxClickIntervalStd) {
                this.maxClickIntervalStd = maxClickIntervalStd;
                return this;
            }

            public NormalizationBounds build() {
                return new NormalizationBounds(this);
            }
        }
    }
}