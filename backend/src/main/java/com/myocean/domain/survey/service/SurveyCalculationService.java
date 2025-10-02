package com.myocean.domain.survey.service;

import com.myocean.domain.survey.entity.SurveyResponse;
import com.myocean.domain.survey.repository.SurveyResponseRepository;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyCalculationService {

    private final SurveyResponseRepository surveyResponseRepository;

    // 기존 정적 계산 로직은 제거하고 데이터베이스 기반으로 계산

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
        // 1) 해당 유저의 설문 응답 조회 (Survey와 BigFiveCode까지 함께 조회)
        List<SurveyResponse> responses = surveyResponseRepository.findByUserIdWithSurvey(userId);
        if (responses.size() != TOTAL_QUESTIONS) {
            throw new GeneralException(ErrorStatus.SURVEY_CALCULATION_FAILED);
        }

        // 2) big_five_code별로 그룹화하여 점수 계산
        return calculateBigFiveScores(responses);
    }

    private Map<String, Integer> calculateBigFiveScores(List<SurveyResponse> responses) {
        // big_five_code별로 응답을 그룹화
        Map<String, List<SurveyResponse>> groupedByBigFive = responses.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getSurvey().getBigFiveCode().getSmallCode()
                ));

        Map<String, Integer> scores = new LinkedHashMap<>();

        for (Map.Entry<String, List<SurveyResponse>> entry : groupedByBigFive.entrySet()) {
            String smallCode = entry.getKey();
            List<SurveyResponse> codeResponses = entry.getValue();

            // 해당 코드의 총점 계산
            int totalScore = 0;
            for (SurveyResponse response : codeResponses) {
                int value = response.getValue().intValue();

                // 역채점 문항인지 확인
                if (response.getSurvey().getIsReverseScored()) {
                    value = reverseLikert(value);  // 6 - value
                }
                totalScore += value;
            }

            scores.put(smallCode, totalScore);
        }

        return scores;
    }

    // ---------------- 계산 로직 ----------------

    private static int reverseLikert(int v) {
        return 6 - v; // 1↔5, 2↔4, 3→3
    }

    // 데이터베이스 기반 계산에서는 별도 검증 불필요 (DB 제약조건으로 보장됨)

    // 기존 정적 파싱 로직 제거 - 이제 데이터베이스에서 동적으로 계산
}
