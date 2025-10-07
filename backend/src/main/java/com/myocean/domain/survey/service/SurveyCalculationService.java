package com.myocean.domain.survey.service;

import com.myocean.domain.survey.entity.SurveyResponse;
import com.myocean.domain.survey.repository.SurveyResponseRepository;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyCalculationService {

    private final SurveyResponseRepository surveyResponseRepository;

    // BigFive 계산 규칙 (NEO-PI-R 기반)
    private static final String BIG_FIVE_SPEC = """
        N1: Anxiety:1, 31, 61, 91
        N2: Anger: 6, 36, 66, 96R
        N3: Depression: 11, 41, 71, 101
        N4: Self-consciousness: 16, 46, 76R, 106R
        N5: Immoderation: 21, 51, 81R, 111R
        N6: Vulnerability: 26, 56R, 86R, 116R
        E1: Friendliness: 2, 32, 62, 92
        E2: Gregariousness: 7, 37, 67R, 97R
        E3: Assertiveness: 12, 42, 72, 102R
        E4: Activity Level: 17, 47, 77, 107
        E5: Excitement Seeking: 22, 52, 82, 112
        E6: Cheerfulness: 27, 57, 87, 117
        O1: Imagination: 3, 33, 63, 93
        O2: Artistic Interests: 8, 38R, 68R, 98R
        O3: Emotionality: 13, 43R, 73R, 103R
        O4: Adventurousness: 18R, 48R, 78R, 108R
        O5: Intellect: 23R, 53R, 83R, 113R
        O6: Liberalism: 28, 58R, 88R, 118R
        A1: Trust: 4, 34, 64, 94R
        A2: Morality: 9R, 39R, 69R, 99R
        A3: Altruism: 14, 44, 74, 104R
        A4: Cooperation: 19R, 49R, 79R, 109R
        A5: Modesty: 24R, 54R, 84R, 114R
        A6: Sympathy: 29, 59, 89, 119R
        C1: Self Efficacy: 5, 35, 65, 95
        C2: Orderliness: 10, 40, 70R, 100R
        C3: Dutifulness: 15, 45, 75R, 105R
        C4: Achievement Striving: 20, 50, 80, 110R
        C5: Self Discipline: 25, 55R, 85R, 115R
        C6: Cautiousness: 30R, 60R, 90R, 120R
        """;

    private static final Map<String, FacetDef> FACETS = parseSpec(BIG_FIVE_SPEC);

    // 설문 관련 상수
    private static final int TOTAL_QUESTIONS = 120;
    private static final int FACET_QUESTION_COUNT = 4;
    private static final int FACET_COUNT = 30;
    private static final int LIKERT_MIN = 1;
    private static final int LIKERT_MAX = 5;
    private static final int FACET_SCORE_MIN = 4;
    private static final int FACET_SCORE_MAX = 20;

    /** 유저의 120문항을 userId로만 조회 → 30개 지표 계산 */
    @Transactional(readOnly = true)
    public Map<String, Integer> calculateBigFiveScoresByUserId(Integer userId) {
        // 1) 해당 유저의 설문 응답 조회
        List<SurveyResponse> responses = surveyResponseRepository.findByUserIdWithSurvey(userId);

        log.info("📋 설문 응답 조회 완료 - userId: {}, 응답 수: {}", userId, responses.size());

        if (responses.size() != TOTAL_QUESTIONS) {
            log.error("📋 설문 응답 수 부족 - 예상: {}, 실제: {}", TOTAL_QUESTIONS, responses.size());
            throw new GeneralException(ErrorStatus.SURVEY_CALCULATION_FAILED);
        }

        // 2) Map<문항번호, 값> 변환
        Map<Integer, Integer> responseMap = responses.stream()
                .collect(Collectors.toMap(
                        r -> Integer.valueOf(r.getSurvey().getId()),
                        r -> r.getValue().intValue(),
                        (a, b) -> a));

        // 3) 유효성 검증(1..120, 1..5)
        validateResponses(responseMap);

        // 4) N1~C6 점수 계산 (각 4~20)
        Map<String, Integer> facetScores = scoreFacets(responseMap);

        log.info("📋 Big5 점수 계산 완료 - 30개 지표: {}", facetScores);
        return facetScores;
    }

    // ---------------- 계산 로직 ----------------

    private Map<String, Integer> scoreFacets(Map<Integer, Integer> responseMap) {
        Map<String, Integer> out = new LinkedHashMap<>();
        for (FacetDef fd : FACETS.values()) {
            int sum = 0;
            for (int q : fd.normal)   sum += responseMap.get(q);
            for (int q : fd.reversed) sum += reverseLikert(responseMap.get(q));
            out.put(fd.code, sum);
        }
        return out;
    }

    private static int reverseLikert(int v) {
        return 6 - v; // 1↔5, 2↔4, 3→3
    }

    private void validateResponses(Map<Integer, Integer> responses) {
        for (int i = 1; i <= TOTAL_QUESTIONS; i++) {
            Integer v = responses.get(i);
            if (v == null) {
                throw new IllegalArgumentException("문항 " + i + " 응답 누락");
            }
            if (v < LIKERT_MIN || v > LIKERT_MAX) {
                throw new IllegalArgumentException("문항 " + i + " 값은 " + LIKERT_MIN + "~" + LIKERT_MAX + " 여야 함. got=" + v);
            }
        }
    }

    // ---------------- SPEC 파싱 ----------------

    private static final class FacetDef {
        final String code;      // N1, E3, ...
        final String label;     // Anxiety, Assertiveness, ...
        final List<Integer> normal = new ArrayList<>();
        final List<Integer> reversed = new ArrayList<>();
        FacetDef(String code, String label) { this.code = code; this.label = label; }
    }

    private static Map<String, FacetDef> parseSpec(String spec) {
        Map<String, FacetDef> map = new LinkedHashMap<>();
        String[] lines = spec.strip().split("\\R");
        Pattern itemPat = Pattern.compile("\\s*(\\d+)(R?)\\s*");

        for (String raw : lines) {
            if (raw.isBlank()) continue;
            String[] parts = raw.split(":");
            if (parts.length < 3) continue;

            String code  = parts[0].trim();   // N1
            String label = parts[1].trim();   // Anxiety
            String items = parts[2].trim();   // "1, 31, 61, 91"
            FacetDef fd = new FacetDef(code, label);

            for (String token : items.split(",")) {
                Matcher m = itemPat.matcher(token);
                if (m.matches()) {
                    int q = Integer.parseInt(m.group(1));
                    boolean reversed = !m.group(2).isEmpty();
                    if (reversed) fd.reversed.add(q);
                    else fd.normal.add(q);
                }
            }
            if (fd.normal.size() + fd.reversed.size() != FACET_QUESTION_COUNT) {
                throw new IllegalStateException(code + " 문항 수가 " + FACET_QUESTION_COUNT + "가 아님");
            }
            map.put(code, fd);
        }
        if (map.size() != FACET_COUNT) {
            throw new IllegalStateException("지표 개수(" + FACET_COUNT + ")가 아님. size=" + map.size());
        }
        return map;
    }
}
