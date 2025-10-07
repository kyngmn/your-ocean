package com.myocean.global.openai.common.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myocean.global.config.OpenAiConfig;
import com.myocean.global.openai.common.dto.OpenAiRequest;
import com.myocean.global.openai.common.dto.OpenAiResponse;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAiClient {

    private final RestTemplate openAiRestTemplate;
    private final OpenAiConfig openAiConfig;
    private final ObjectMapper objectMapper;

    public OpenAiResponse chatCompletion(OpenAiRequest request) {
        log.info("OpenAI API 호출 시작");
        log.info("API URL: {}", openAiConfig.getApiUrl() + "/chat/completions");
        log.info("모델: {}", openAiConfig.getModel());
        log.info("API 키 설정 여부: {}", openAiConfig.getApiKey() != null && !openAiConfig.getApiKey().equals("your-openai-api-key"));
        log.debug("요청 데이터: {}", request);

        try {
            String url = openAiConfig.getApiUrl() + "/chat/completions";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + openAiConfig.getApiKey());
            headers.setContentType(MediaType.APPLICATION_JSON);

            String json = objectMapper.writeValueAsString(request);
            HttpEntity<String> entity = new HttpEntity<>(json, headers);

            log.info("OpenAI API 요청 전송 중...");
            log.debug("요청 JSON: {}", json);
            log.info("전체 URL: {}", url);

            ResponseEntity<String> response = openAiRestTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            log.info("OpenAI API 응답 상태: {}", response.getStatusCode());
            log.debug("OpenAI API 응답 본문: {}", response.getBody());

            if (response.getBody() != null) {
                OpenAiResponse openAiResponse = objectMapper.readValue(response.getBody(), OpenAiResponse.class);
                log.info("OpenAI API 호출 성공");
                return openAiResponse;
            } else {
                log.error("OpenAI API 응답 본문이 비어있음");
                throw new GeneralException(ErrorStatus.OPENAI_RESPONSE_EMPTY);
            }

        } catch (Exception e) {
            log.error("OpenAI API 호출 실패: {}", e.getMessage(), e);
            throw new GeneralException(ErrorStatus.OPENAI_API_CALL_FAILED, e);
        }
    }
}