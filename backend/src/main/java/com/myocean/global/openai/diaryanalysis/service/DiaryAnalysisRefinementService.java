package com.myocean.global.openai.diaryanalysis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myocean.global.config.OpenAiConfig;
import com.myocean.global.openai.common.client.OpenAiClient;
import com.myocean.global.openai.common.dto.OpenAiRequest;
import com.myocean.global.openai.common.dto.OpenAiResponse;
import com.myocean.global.openai.diaryanalysis.prompt.DiaryAnalysisPrompts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryAnalysisRefinementService {

    private final OpenAiClient openAiClient;
    private final OpenAiConfig openAiConfig;
    private final ObjectMapper objectMapper;

    public Map<String, Object> refineAnalysisResult(String title, String content, Map<String, Object> originalAnalysis) {
        try {
            log.info("OpenAI로 다이어리 분석 결과 다듬기 시작 - title: {}", title);

            // 원본 분석 결과를 문자열로 변환
            String originalAnalysisString = convertAnalysisToString(originalAnalysis);

            // 프롬프트 생성
            String prompt = DiaryAnalysisPrompts.createDiaryAnalysisPrompt(title, content, originalAnalysisString);

            // OpenAI 요청 생성
            List<OpenAiRequest.Message> messages = List.of(
                OpenAiRequest.Message.user(prompt)
            );

            // 핵심 기능인 대화형 분석이므로 standard 모델 사용 (품질 우선)
            OpenAiRequest request = OpenAiRequest.create(openAiConfig.getStandardModel(), messages);

            // OpenAI API 호출
            OpenAiResponse response = openAiClient.chatCompletion(request);
            String responseContent = response.getContent();

            if (responseContent != null && !responseContent.trim().isEmpty()) {
                // JSON 응답 파싱
                Map<String, Object> refinedData = parseJsonResponse(responseContent);

                if (refinedData != null) {
                    log.info("OpenAI 다이어리 분석 결과 다듬기 완료");

                    // 성공 응답 형태로 반환
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", true);
                    result.put("data", refinedData);
                    return result;
                } else {
                    log.warn("OpenAI 응답 JSON 파싱 실패, 원본 분석 결과 반환");
                    return originalAnalysis;
                }
            } else {
                log.warn("OpenAI 응답이 비어있음, 원본 분석 결과 반환");
                return originalAnalysis;
            }

        } catch (Exception e) {
            log.error("OpenAI 다이어리 분석 결과 다듬기 실패: {}", e.getMessage(), e);
            return originalAnalysis;
        }
    }

    private String convertAnalysisToString(Map<String, Object> analysis) {
        try {
            return objectMapper.writeValueAsString(analysis);
        } catch (Exception e) {
            log.error("분석 결과 문자열 변환 실패: {}", e.getMessage());
            return analysis.toString();
        }
    }

    private Map<String, Object> parseJsonResponse(String jsonResponse) {
        try {
            // JSON 형태만 추출 (혹시 다른 텍스트가 포함된 경우 대비)
            String cleanedJson = extractJsonFromResponse(jsonResponse);

            @SuppressWarnings("unchecked")
            Map<String, Object> parsedData = objectMapper.readValue(cleanedJson, Map.class);

            // agent_responses 필드가 있는지 확인
            if (parsedData.containsKey("agent_responses")) {
                Map<String, Object> agentResponses = (Map<String, Object>) parsedData.get("agent_responses");
                // OCEAN 요소가 모두 있는지 확인
                if (agentResponses != null &&
                    agentResponses.containsKey("Openness") && agentResponses.containsKey("Conscientiousness") &&
                    agentResponses.containsKey("Extraversion") && agentResponses.containsKey("Agreeableness") &&
                    agentResponses.containsKey("Neuroticism")) {
                    return parsedData;
                } else {
                    log.warn("응답에 OCEAN 요소가 모두 포함되지 않음");
                    return null;
                }
            } else {
                log.warn("응답에 agent_responses 필드가 없음");
                return null;
            }

        } catch (Exception e) {
            log.error("JSON 응답 파싱 실패: {}", e.getMessage());
            return null;
        }
    }

    private String extractJsonFromResponse(String response) {
        // { 로 시작하고 } 로 끝나는 첫 번째 JSON 블록 추출
        int startIndex = response.indexOf('{');
        int endIndex = response.lastIndexOf('}');

        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            return response.substring(startIndex, endIndex + 1);
        }

        return response.trim();
    }
}