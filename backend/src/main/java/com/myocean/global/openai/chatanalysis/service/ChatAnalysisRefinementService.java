package com.myocean.global.openai.chatanalysis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myocean.global.config.OpenAiConfig;
import com.myocean.global.openai.common.client.OpenAiClient;
import com.myocean.global.openai.common.dto.OpenAiRequest;
import com.myocean.global.openai.common.dto.OpenAiResponse;
import com.myocean.global.openai.chatanalysis.prompt.ChatAnalysisPrompts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatAnalysisRefinementService {

    private final OpenAiClient openAiClient;
    private final OpenAiConfig openAiConfig;
    private final ObjectMapper objectMapper;

    public Map<String, Object> refineChatAnalysisResult(String message, Map<String, Object> originalAnalysis) {
        try {
            log.info("OpenAI로 채팅 분석 결과 다듬기 시작 - message: {}",
                    message.length() > 50 ? message.substring(0, 50) + "..." : message);

            // 원본 분석 결과를 문자열로 변환
            String originalAnalysisString = convertAnalysisToString(originalAnalysis);

            // 채팅용 프롬프트 생성
            String prompt = ChatAnalysisPrompts.createChatAnalysisPrompt(message, originalAnalysisString);

            // OpenAI 요청 생성
            List<OpenAiRequest.Message> messages = List.of(
                OpenAiRequest.Message.user(prompt)
            );

            OpenAiRequest request = OpenAiRequest.create(openAiConfig.getStandardModel(), messages);

            // OpenAI API 호출
            OpenAiResponse response = openAiClient.chatCompletion(request);
            String responseContent = response.getContent();

            if (responseContent != null && !responseContent.trim().isEmpty()) {
                log.info("==================== OpenAI 원본 응답 ====================");
                log.info("OpenAI 응답: {}", responseContent);
                log.info("========================================================");

                // JSON 응답 파싱
                Map<String, Object> refinedData = parseJsonResponse(responseContent);

                if (refinedData != null) {
                    log.info("==================== OpenAI 파싱 결과 ====================");
                    log.info("파싱된 데이터: {}", refinedData);
                    log.info("========================================================");
                    log.info("OpenAI 채팅 분석 결과 다듬기 완료");

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
            log.error("OpenAI 채팅 분석 결과 다듬기 실패: {}", e.getMessage(), e);
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

                // 채팅의 경우 상위 3개만 확인하면 됨 (모든 OCEAN 요소가 필요하지 않음)
                if (agentResponses != null && !agentResponses.isEmpty()) {
                    return parsedData;
                } else {
                    log.warn("응답에 agent_responses가 비어있음");
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
        // ```json 코드블록이 있는 경우 먼저 처리
        String trimmed = response.trim();
        if (trimmed.startsWith("```json") && trimmed.endsWith("```")) {
            // ```json과 ```를 제거
            String jsonContent = trimmed.substring(7, trimmed.length() - 3).trim();
            log.debug("JSON 코드블록에서 추출: {}", jsonContent.substring(0, Math.min(100, jsonContent.length())));
            return jsonContent;
        }

        // 일반적인 { } JSON 블록 추출
        int startIndex = response.indexOf('{');
        int endIndex = response.lastIndexOf('}');

        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            String jsonContent = response.substring(startIndex, endIndex + 1);
            log.debug("JSON 블록에서 추출: {}", jsonContent.substring(0, Math.min(100, jsonContent.length())));
            return jsonContent;
        }

        log.debug("원본 응답 반환: {}", trimmed.substring(0, Math.min(100, trimmed.length())));
        return trimmed;
    }
}