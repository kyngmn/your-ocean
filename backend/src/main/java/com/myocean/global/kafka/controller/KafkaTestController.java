package com.myocean.global.kafka.controller;

import com.myocean.domain.gng.dto.request.GngResponseCreateRequest;
import com.myocean.global.auth.CustomUserDetails;
import com.myocean.global.auth.LoginMember;
import com.myocean.global.kafka.service.KafkaProducerService;
import com.myocean.response.ApiResponse;
import com.myocean.response.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/kafka")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Kafka Test", description = "Kafka 연동 테스트 API")
public class KafkaTestController {

    private final KafkaProducerService kafkaProducerService;

    @PostMapping("/{sessionId}/gng/rounds/{roundIndex}/clicks")
    @Operation(summary = "GNG 게임 응답 Kafka 전송", description = "기존 GNG API와 동일한 입력으로 RDB 대신 Kafka에 저장")
    public ApiResponse<String> saveGngResponseToKafka(
            @LoginMember CustomUserDetails userDetails,
            @PathVariable Long sessionId,
            @PathVariable Integer roundIndex,
            @Valid @RequestBody GngResponseCreateRequest request) {

        Integer userId = userDetails.getUser().getId();

        log.info("📤 GNG 게임 응답 Kafka 전송 - User: {}, Session: {}, Round: {}", userId, sessionId, roundIndex);

        try {
            // 반응 시간 계산
            Long reactionTimeMs = null;
            if (request.stimulusStartedAt() != null && request.respondedAt() != null) {
                reactionTimeMs = java.time.Duration.between(request.stimulusStartedAt(), request.respondedAt()).toMillis();
            }

            // test_kafka.py 구조를 기반으로 GNG 데이터 통합
            Map<String, Object> kafkaMessage = new HashMap<>();

            // 기존 토픽 구조 유지
            kafkaMessage.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            kafkaMessage.put("level", "INFO");
            kafkaMessage.put("service", "myocean-backend");
            kafkaMessage.put("user_id", userId.toString());
            kafkaMessage.put("test_id", String.format("gng-game-%d-%d-%d", sessionId, roundIndex, System.currentTimeMillis()));

            // message 필드에 GNG 게임 정보 구조화
            String messageContent = String.format(
                "GNG Game Response - User %d played round %d in session %d | Stimulus: %s | Success: %s | Reaction: %s",
                userId, roundIndex, sessionId,
                request.stimulusType(),
                request.isSucceeded(),
                reactionTimeMs != null ? reactionTimeMs + "ms" : "no response"
            );
            kafkaMessage.put("message", messageContent);

            // GNG 게임 세부 데이터 추가 (기존 구조 확장)
            kafkaMessage.put("game_type", "GNG");
            kafkaMessage.put("session_id", sessionId);
            kafkaMessage.put("round", roundIndex);
            kafkaMessage.put("stimulus_type", request.stimulusType().toString());
            kafkaMessage.put("stimulus_started_at", request.stimulusStartedAt() != null ?
                request.stimulusStartedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
            kafkaMessage.put("responded_at", request.respondedAt() != null ?
                request.respondedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
            kafkaMessage.put("is_succeeded", request.isSucceeded());
            kafkaMessage.put("reaction_time_ms", reactionTimeMs);

            // Kafka로 전송
            kafkaProducerService.sendGameLog("GNG", userId, sessionId, "gng_response", kafkaMessage);

            log.info("✅ GNG 게임 응답 Kafka 전송 성공 - User: {}, Session: {}, Round: {}, 반응시간: {}ms",
                userId, sessionId, roundIndex, reactionTimeMs);

            return ApiResponse.onSuccess(SuccessStatus.GAME_RESPONSE_SAVED,
                SuccessStatus.GAME_RESPONSE_SAVED.getMessage());

        } catch (Exception e) {
            log.error("❌ GNG 게임 응답 Kafka 전송 실패 - User: {}, Session: {}, Round: {}, Error: {}",
                userId, sessionId, roundIndex, e.getMessage(), e);
            return ApiResponse.onSuccess(SuccessStatus.OK, "GNG Kafka 전송 중 오류 발생: " + e.getMessage());
        }
    }

    @PostMapping("/test/gng-batch")
    @Operation(summary = "GNG 게임 배치 테스트", description = "여러 라운드의 GNG 데이터를 한번에 전송")
    public ApiResponse<String> testGngBatchKafkaLog(@LoginMember CustomUserDetails userDetails) {

        Integer userId = userDetails.getUser().getId();
        Long sessionId = System.currentTimeMillis();

        try {
            // 5라운드 테스트 데이터 전송
            for (int round = 1; round <= 5; round++) {
                LocalDateTime stimulusStartedAt = LocalDateTime.now().minusSeconds(round * 2);
                LocalDateTime respondedAt = LocalDateTime.now().minusSeconds(round * 2 - 1);
                String stimulusType = (round % 2 == 0) ? "NOGO" : "GO";
                Boolean isSucceeded = (round % 3 != 0); // 3의 배수는 실패로 설정

                long reactionTimeMs = java.time.Duration.between(stimulusStartedAt, respondedAt).toMillis();

                Map<String, Object> kafkaMessage = new HashMap<>();
                kafkaMessage.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                kafkaMessage.put("level", "INFO");
                kafkaMessage.put("service", "myocean-backend");
                kafkaMessage.put("user_id", userId.toString());
                kafkaMessage.put("test_id", String.format("gng-batch-%d-%d-%d", sessionId, round, System.currentTimeMillis()));

                kafkaMessage.put("message", String.format(
                    "GNG_BATCH_TEST|user_id:%d|session_id:%d|round:%d|stimulus:%s|reaction_time:%dms|success:%b",
                    userId, sessionId, round, stimulusType, reactionTimeMs, isSucceeded
                ));

                kafkaProducerService.sendGameLog("GNG_BATCH", userId, sessionId, "batch_test_round_" + round, kafkaMessage);

                Thread.sleep(100); // 메시지 간 간격
            }

            return ApiResponse.onSuccess(SuccessStatus.OK,
                String.format("GNG 배치 테스트 완료! 5라운드 데이터가 전송되었습니다. (Session: %d)", sessionId));

        } catch (Exception e) {
            log.error("❌ GNG 배치 테스트 실패: {}", e.getMessage(), e);
            return ApiResponse.onSuccess(SuccessStatus.OK, "GNG 배치 테스트 중 오류 발생: " + e.getMessage());
        }
    }
}