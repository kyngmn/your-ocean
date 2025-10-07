package com.myocean.domain.big5.service;

import com.myocean.domain.report.service.ReportService;
import com.myocean.global.openai.personality.service.PersonalityComparisonService;
import com.myocean.global.openai.personality.dto.PersonalityComparisonRequest;
import com.myocean.global.openai.personality.dto.PersonalityInsightsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonalityAnalysisService {

    private final ReportService reportService;
    private final PersonalityComparisonService personalityComparisonService;

    @Transactional
    public void generatePersonalityReport(Integer userId, Map<String, Integer> gameScores) {
        log.info("=== PersonalityAnalysisService 시작 - userId: {}, gameScores: {} ===", userId, gameScores);

        // Self 설문 결과 조회
        Map<String, Integer> selfScores = getSelfScores(userId);
        log.info("Self 설문 점수 조회 완료 - selfScores: {}", selfScores);

        // OpenAI 인사이트와 함께 FINAL 리포트 생성
        generateFinalReport(userId, gameScores, selfScores);

        log.info("=== PersonalityAnalysisService 완료 - userId: {} ===", userId);
    }

    private Map<String, Integer> getSelfScores(Integer userId) {
        try {
            Map<String, Integer> detailedScores = reportService.getSelfBig5Scores(userId);
            log.info("📊 Self 세부 점수 조회 완료 - detailedScores: {}", detailedScores);

            // 30개 세부 지표를 5개 대분류로 합산
            return aggregateToMainFactors(detailedScores);
        } catch (Exception e) {
            log.warn("📊 Self 점수 조회 실패, 기본값 사용: {}", e.getMessage());
            return Map.of("O", 50, "C", 50, "E", 50, "A", 50, "N", 50); // 중간값으로 fallback
        }
    }

    private Map<String, Integer> aggregateToMainFactors(Map<String, Integer> detailedScores) {
        // O1~O6 합산하여 O로
        int O = detailedScores.getOrDefault("O1", 0) + detailedScores.getOrDefault("O2", 0) +
                detailedScores.getOrDefault("O3", 0) + detailedScores.getOrDefault("O4", 0) +
                detailedScores.getOrDefault("O5", 0) + detailedScores.getOrDefault("O6", 0);

        // C1~C6 합산하여 C로
        int C = detailedScores.getOrDefault("C1", 0) + detailedScores.getOrDefault("C2", 0) +
                detailedScores.getOrDefault("C3", 0) + detailedScores.getOrDefault("C4", 0) +
                detailedScores.getOrDefault("C5", 0) + detailedScores.getOrDefault("C6", 0);

        // E1~E6 합산하여 E로
        int E = detailedScores.getOrDefault("E1", 0) + detailedScores.getOrDefault("E2", 0) +
                detailedScores.getOrDefault("E3", 0) + detailedScores.getOrDefault("E4", 0) +
                detailedScores.getOrDefault("E5", 0) + detailedScores.getOrDefault("E6", 0);

        // A1~A6 합산하여 A로
        int A = detailedScores.getOrDefault("A1", 0) + detailedScores.getOrDefault("A2", 0) +
                detailedScores.getOrDefault("A3", 0) + detailedScores.getOrDefault("A4", 0) +
                detailedScores.getOrDefault("A5", 0) + detailedScores.getOrDefault("A6", 0);

        // N1~N6 합산하여 N으로
        int N = detailedScores.getOrDefault("N1", 0) + detailedScores.getOrDefault("N2", 0) +
                detailedScores.getOrDefault("N3", 0) + detailedScores.getOrDefault("N4", 0) +
                detailedScores.getOrDefault("N5", 0) + detailedScores.getOrDefault("N6", 0);

        Map<String, Integer> aggregated = Map.of("O", O, "C", C, "E", E, "A", A, "N", N);
        log.info("📊 Self 점수 5개 대분류 집계 완료 - aggregated: {}", aggregated);

        // 타입 디버깅
        log.info("📊 타입 확인 - O:{} (Integer), C:{} (Integer), E:{} (Integer), A:{} (Integer), N:{} (Integer)",
                O, C, E, A, N);

        return aggregated;
    }

    private void generateFinalReport(Integer userId, Map<String, Integer> gameScores, Map<String, Integer> selfScores) {
        log.info(">>> OpenAI 분석 시작 - Self vs Game 비교 시작");

        // OpenAI 감성 분석 시도 (실패 시 기본값 반환)
        PersonalityInsightsResponse insights = generateInsights(gameScores, selfScores);

        log.info(">>> OpenAI 분석 완료 - insights: {}", insights);

        // 항상 insights와 함께 저장
        saveFinalReportWithInsights(userId, gameScores, insights);

        log.info(">>> FINAL 리포트 저장 완료");
    }

    private PersonalityInsightsResponse generateInsights(Map<String, Integer> gameScores, Map<String, Integer> selfScores) {
        try {
            log.info("🚀 OpenAI API 호출 준비 중 - selfScores: {}, gameScores: {}", selfScores, gameScores);

            PersonalityComparisonRequest request = PersonalityComparisonRequest.of(selfScores, gameScores);
            log.info("🚀 OpenAI API 호출 시작 - PersonalityComparisonService.comparePersonalities()");

            PersonalityInsightsResponse result = personalityComparisonService.comparePersonalities(request);

            log.info("✅ OpenAI API 호출 성공 - 분석 결과 받음");
            return result;
        } catch (Exception e) {
            log.error("❌ OpenAI API 호출 실패 - 기본값 사용: {}", e.getMessage(), e);
            return createDefaultInsights();
        }
    }

    private PersonalityInsightsResponse createDefaultInsights() {
        PersonalityInsightsResponse.Insights defaultInsights = new PersonalityInsightsResponse.Insights(
                "당신만의 독특한 매력이 있어요 ✨",
                "게임을 통해 진짜 모습을 발견했네요",
                "행동으로 드러난 당신의 진정한 성격"
        );
        return new PersonalityInsightsResponse(
                "당신은 여러 가지 빛깔을 가진 보석 같은 사람이에요 💎",
                defaultInsights
        );
    }

    private void saveFinalReportWithInsights(Integer userId, Map<String, Integer> gameScores, PersonalityInsightsResponse insights) {
        reportService.saveFinalReportWithInsights(userId, gameScores, insights);
    }
}