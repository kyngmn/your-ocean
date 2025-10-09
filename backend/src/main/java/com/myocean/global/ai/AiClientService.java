package com.myocean.global.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiClientService {

    private final RestTemplate restTemplate;

    @Value("${ai.server.url:http://localhost:8000}")
    private String aiServerUrl;

    public Map<String, Object> chatWithAi(Integer userId, String message, String chatType, Integer relatedId) {
        try {
            log.info("AI 서버로 채팅 요청 - userId: {}, chatType: {}, message: {}", 
                    userId, chatType, message.substring(0, Math.min(50, message.length())));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Id", userId.toString());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("user_id", userId);
            requestBody.put("message", message);
            requestBody.put("chat_type", chatType);
            if (relatedId != null) {
                if ("diary".equals(chatType)) {
                    requestBody.put("diary_id", relatedId);
                } else if ("friend".equals(chatType)) {
                    requestBody.put("room_id", relatedId);
                }
            }
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            String endpoint;
            if ("diary".equals(chatType)) {
                endpoint = "/api/v1/diaries";
            } else if ("friend".equals(chatType)) {
                endpoint = "/api/v1/friend-chat/send";
            } else {
                endpoint = "/api/v1/my-chat/send";
            }

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    aiServerUrl + endpoint,
                    entity,
                    Map.class
            );

            if (response.getBody() != null && (Boolean) response.getBody().get("success")) {
                log.info("AI 채팅 성공 - userId: {}", userId);
                return response.getBody(); // 전체 응답 반환 (agent_responses 포함)
            } else {
                log.error("AI 채팅 실패 - 응답이 비어있거나 실패");
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "죄송합니다. 현재 AI 서비스에 일시적인 문제가 있습니다.");
                return errorResponse;
            }

        } catch (Exception e) {
            log.error("AI 서버 호출 중 오류 발생: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "죄송합니다. AI 서비스 연결에 문제가 있습니다. 잠시 후 다시 시도해주세요.");
            return errorResponse;
        }
    }

    public Map<String, Object> analyzeDiary(Integer userId, Integer diaryId, String content, String title) {
        try {
            log.info("AI 서버로 다이어리 분석 요청 - userId: {}, diaryId: {}", userId, diaryId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Id", userId.toString());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("user_id", userId);
            requestBody.put("diary_id", diaryId);
            requestBody.put("content", content);
            requestBody.put("title", title);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    aiServerUrl + "/ai/analyze/diary",
                    entity,
                    Map.class
            );

            log.info("AI 서버 응답 - diaryId: {}, response: {}", diaryId, response.getBody());

            if (response.getBody() != null && (Boolean) response.getBody().get("success")) {
                return response.getBody();
            } else {
                throw new RuntimeException("AI 분석 실패");
            }

        } catch (Exception e) {
            throw new RuntimeException("AI 서버 연결 실패", e);
        }
    }

    public Map<String, Object> analyzeChatMessage(Integer userId, Long messageId, String message) {
        try {
            log.info("AI 서버로 채팅 메시지 분석 요청 - userId: {}, messageId: {}", userId, messageId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Id", userId.toString());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("user_id", userId);
            requestBody.put("message", message);
            requestBody.put("chat_type", "my_chat");
            requestBody.put("message_id", messageId);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    aiServerUrl + "/ai/chat",
                    entity,
                    Map.class
            );

            if (response.getBody() != null) {
                log.info("==================== AI 서버 응답 ====================");
                log.info("AI 서버 응답: {}", response.getBody());
                log.info("====================================================");

                // AI 서버에서 직접 분석 결과를 반환하므로 success 래핑 추가
                Map<String, Object> wrappedResponse = new HashMap<>();
                wrappedResponse.put("success", true);
                wrappedResponse.put("data", response.getBody());
                return wrappedResponse;
            } else {
                throw new RuntimeException("AI 채팅 분석 실패");
            }

        } catch (Exception e) {
            throw new RuntimeException("AI 채팅 분석 서버 연결 실패", e);
        }
    }

    public boolean checkAiServerHealth() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    aiServerUrl + "/health",
                    String.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("AI 서버 헬스체크 실패: {}", e.getMessage());
            return false;
        }
    }
}