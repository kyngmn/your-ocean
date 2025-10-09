package com.myocean.domain.diary.util;

import com.myocean.domain.diary.constants.OceanConstants;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 서버로부터 받은 다이어리 분석 결과를 파싱하는 유틸리티
 */
@UtilityClass
@Slf4j
public class DiaryAnalysisParser {

    /**
     * AI 응답에서 실제 데이터 추출
     * OpenAI 처리 후 응답인 경우 data 필드에서 추출
     */
    public static Map<String, Object> extractActualData(Map<String, Object> analysisResult) {
        if (analysisResult.containsKey("data") && analysisResult.get("data") instanceof Map) {
            return (Map<String, Object>) analysisResult.get("data");
        }
        return analysisResult;
    }

    /**
     * agent_responses 추출
     */
    public static Map<String, Object> extractAgentResponses(Map<String, Object> actualData, Integer userId, Integer diaryId) {
        Object agentResponsesObj = actualData.get("agent_responses");
        if (!(agentResponsesObj instanceof Map)) {
            log.error("agent_responses가 Map 타입이 아니거나 비어있음 - userId: {}, diaryId: {}", userId, diaryId);
            return null;
        }
        return (Map<String, Object>) agentResponsesObj;
    }

    /**
     * Big5 점수 파싱
     */
    public static Map<String, Double> parseBig5Scores(Map<String, Object> analysisData) {
        Map<String, Double> big5Scores = new HashMap<>();
        Object big5DataObj = analysisData.get("big5_scores");

        if (big5DataObj instanceof Map) {
            Map<String, Object> big5Data = (Map<String, Object>) big5DataObj;
            log.debug("Big5 scores 원본: {}", big5Data);

            for (Map.Entry<String, Object> entry : big5Data.entrySet()) {
                String originalKey = entry.getKey();
                String mappedKey = OceanConstants.BIG5_KEY_NORMALIZER.getOrDefault(originalKey,
                        OceanConstants.BIG5_KEY_NORMALIZER.getOrDefault(originalKey.toLowerCase(), originalKey));

                if (entry.getValue() instanceof Number) {
                    big5Scores.put(mappedKey, ((Number) entry.getValue()).doubleValue());
                    log.debug("Big5 매핑: {} -> {} = {}", originalKey, mappedKey, entry.getValue());
                }
            }
        } else {
            log.warn("big5_scores가 Map 타입이 아님: {}", big5DataObj != null ? big5DataObj.getClass().getSimpleName() : "null");
        }

        return big5Scores;
    }

    /**
     * domain_classification 추출
     */
    public static String parseDomainClassification(Map<String, Object> analysisData) {
        return (String) analysisData.get("domain_classification");
    }

    /**
     * final_conclusion 추출
     */
    public static String parseFinalConclusion(Map<String, Object> analysisData) {
        return (String) analysisData.get("final_conclusion");
    }

    /**
     * keywords 파싱
     */
    public static List<String> parseKeywords(Map<String, Object> analysisData) {
        List<String> keywords = new ArrayList<>();
        Object keywordsData = analysisData.get("keywords");

        if (keywordsData instanceof List) {
            List<?> keywordsList = (List<?>) keywordsData;
            for (Object keyword : keywordsList) {
                if (keyword instanceof String) {
                    keywords.add((String) keyword);
                }
            }
        }
        return keywords;
    }
}
