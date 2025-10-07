package com.myocean.global.openai.personality.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myocean.global.openai.common.client.OpenAiClient;
import com.myocean.global.config.OpenAiConfig;
import com.myocean.global.openai.common.dto.OpenAiRequest.Message;
import com.myocean.global.openai.common.dto.OpenAiRequest;
import com.myocean.global.openai.common.dto.OpenAiResponse;
import com.myocean.global.openai.personality.dto.PersonalityComparisonRequest;
import com.myocean.global.openai.personality.dto.PersonalityInsightsResponse;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import com.myocean.global.openai.personality.prompt.PersonalityPrompts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonalityComparisonService {

    private final OpenAiClient openAiClient;
    private final OpenAiConfig openAiConfig;
    private final ObjectMapper objectMapper;

    public PersonalityInsightsResponse comparePersonalities(PersonalityComparisonRequest request) {
        try {

            // 프롬프트 생성
            log.info("🔍 프롬프트 생성 전 - selfScores: {}, gameScores: {}",
                    request.getSelfScores(), request.getGameScores());

            String prompt = PersonalityPrompts.createComparisonPrompt(
                    request.getSelfScores(),
                    request.getGameScores()
            );

            log.info("🔍 프롬프트 생성 완료");

            // OpenAI 요청 생성
            List<Message> messages = List.of(
                    Message.system("당신은 성격 분석 전문가입니다. 항상 정확한 JSON 형식으로 응답하세요."),
                    Message.user(prompt)
            );

            OpenAiRequest openAiRequest = OpenAiRequest.createJsonMode(
                    openAiConfig.getStandardModel(),
                    messages
            );

            // OpenAI API 호출
            OpenAiResponse response = openAiClient.chatCompletion(openAiRequest);
            String content = response.getContent();
            log.info("🔍 OpenAI 원본 응답: {}", content);

            if (content == null || content.trim().isEmpty()) {
                log.error("🔍 OpenAI 응답이 비어있음");
                throw new GeneralException(ErrorStatus.OPENAI_RESPONSE_EMPTY);
            }

            // 마크다운 코드 블록 제거
            String cleanedContent = content;
            if (content.startsWith("```json")) {
                cleanedContent = content.substring(7); // "```json" 제거
            }
            if (cleanedContent.endsWith("```")) {
                cleanedContent = cleanedContent.substring(0, cleanedContent.length() - 3); // "```" 제거
            }
            cleanedContent = cleanedContent.trim();

            log.info("🔍 정리된 JSON: {}", cleanedContent);

            // JSON 파싱
            PersonalityInsightsResponse result = objectMapper.readValue(cleanedContent, PersonalityInsightsResponse.class);
            log.info("🔍 파싱된 결과: {}", result);

            return result;

        } catch (GeneralException e) {
            throw e;
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("OpenAI")) {
                throw new GeneralException(ErrorStatus.OPENAI_API_CALL_FAILED, e);
            } else {
                throw new GeneralException(ErrorStatus.OPENAI_JSON_PARSE_FAILED, e);
            }
        }
    }
}