package com.myocean.global.kafka.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "s3-test-logs";

    public void sendGameLog(String gameType, Integer userId, Long sessionId, String action, Object data) {
        Map<String, Object> logMessage = createLogMessage(gameType, userId, sessionId, action, data);

        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(TOPIC, logMessage);

            future.thenAccept(result -> {
                log.info("✅ Kafka 메시지 전송 성공! Topic: {}, Partition: {}, Offset: {}",
                    TOPIC, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            }).exceptionally(throwable -> {
                log.error("❌ Kafka 메시지 전송 실패: {}", throwable.getMessage());
                return null;
            });

        } catch (Exception e) {
            log.error("❌ Kafka 전송 중 예외 발생: {}", e.getMessage(), e);
        }
    }

    private Map<String, Object> createLogMessage(String gameType, Integer userId, Long sessionId, String action, Object data) {
        Map<String, Object> logMessage = new HashMap<>();
        logMessage.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        logMessage.put("level", "INFO");
        logMessage.put("service", "myocean-backend");
        logMessage.put("game_type", gameType);
        logMessage.put("user_id", userId);
        logMessage.put("session_id", sessionId);
        logMessage.put("action", action);
        logMessage.put("data", data);
        logMessage.put("message", String.format("Game %s - User %d performed %s in session %d",
            gameType, userId, action, sessionId));

        return logMessage;
    }
}