package com.myocean.global.openai.dailymessage.service;

import com.myocean.global.config.OpenAiConfig;
import com.myocean.global.enums.BigCode;
import com.myocean.global.openai.common.client.OpenAiClient;
import com.myocean.global.openai.common.dto.OpenAiRequest;
import com.myocean.global.openai.common.dto.OpenAiResponse;
import com.myocean.global.openai.dailymessage.dto.DailyMessageResponse;
import com.myocean.global.openai.dailymessage.prompt.DailyMessagePrompts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyMessageService {

    private final RedisTemplate<String, String> redisTemplate;
    private final OpenAiClient openAiClient;
    private final OpenAiConfig openAiConfig;

    public DailyMessageResponse getDailyMessage() {
        // 현재 시간 기반으로 시간대 계산 
        LocalDateTime now = LocalDateTime.now();
        String timeSlot = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"));

        // Redis 키 생성 (시간대만으로 키 생성)
        String redisKey = "daily_message:" + timeSlot;

        // Redis에서 캐시된 데이터 확인
        String cachedData = redisTemplate.opsForValue().get(redisKey);

        if (cachedData != null) {
            log.info("캐시된 데이터 반환: {}", redisKey);
            // 캐시된 데이터에서 특성과 메시지 분리 (format: "trait|message")
            String[] parts = cachedData.split("\\|", 2);
            BigCode bigCode = BigCode.fromString(parts[0]);
            String message = parts[1];

            return new DailyMessageResponse(
                bigCode,
                message,
                now.format(DateTimeFormatter.ofPattern("HH:mm"))
            );
        }

        // 캐시 미스시 새로 생성
        // 시간 기반으로 고정된 특성 선택 (같은 시간대에는 항상 같은 특성)
        BigCode selectedTrait = getTraitByTime(now);

        // OpenAI 호출하여 새 메시지 생성
        String newMessage = generateMessageWithOpenAI(selectedTrait);

        // Redis에 특성과 메시지를 함께 저장 (format: "trait|message")
        String dataToCache = selectedTrait.name() + "|" + newMessage;
        redisTemplate.opsForValue().set(redisKey, dataToCache, 5, TimeUnit.MINUTES);
        log.info("새 데이터 생성 및 캐시 저장: {}", redisKey);

        return new DailyMessageResponse(
            selectedTrait,
            newMessage,
            now.format(DateTimeFormatter.ofPattern("HH:mm"))
        );
    }

    private BigCode getTraitByTime(LocalDateTime time) {
        // 시간 기반으로 고정된 특성 선택 (분 단위로 순환)
        int minute = time.getMinute();
        BigCode[] bigCodes = BigCode.values();
        int index = minute % bigCodes.length; // 0-4 순환
        return bigCodes[index];
    }

    private String generateMessageWithOpenAI(BigCode bigCode) {
        try {
            // DailyMessagePrompts를 사용하여 프롬프트 생성
            String prompt = DailyMessagePrompts.createDailyMessagePrompt(bigCode.name());

            // OpenAI 요청 생성
            List<OpenAiRequest.Message> messages = List.of(
                OpenAiRequest.Message.user(prompt)
            );

            // 간단하지만 사용자 경험 중요한 메시지 생성이므로 light 모델 사용
            OpenAiRequest request = OpenAiRequest.create(openAiConfig.getLightModel(), messages);

            // OpenAI API 호출
            log.info("특성 {}({})에 대한 오늘의 말 생성 중...", bigCode.name(), bigCode.getKoreanName());
            OpenAiResponse response = openAiClient.chatCompletion(request);

            String generatedMessage = response.getContent();
            if (generatedMessage != null && !generatedMessage.trim().isEmpty()) {
                // 앞뒤 따옴표 제거
                String cleanedMessage = generatedMessage.trim()
                    .replaceAll("^[\"']", "")  // 시작 따옴표 제거
                    .replaceAll("[\"']$", ""); // 끝 따옴표 제거
                log.info("특성 {}({})에 대한 오늘의 말 생성 완료: {}", bigCode.name(), bigCode.getKoreanName(), cleanedMessage);
                return cleanedMessage;
            } else {
                log.warn("OpenAI 응답이 비어있음. 기본 메시지 사용");
                return getFallbackMessage(bigCode);
            }

        } catch (Exception e) {
            log.error("OpenAI 호출 실패: {}", e.getMessage(), e);
            return getFallbackMessage(bigCode);
        }
    }

    private String getFallbackMessage(BigCode bigCode) {
        // OpenAI 호출 실패시 사용할 기본 메시지
        return switch (bigCode) {
            case O -> "새로운 탐험을 해보는 건 어떨까요? 🌟";
            case C -> "오늘도 차근차근, 하나씩 완성해나가는 하루가 되길! 📝";
            case E -> "오늘은 누군가와 만나서 즐거운 시간 보내보세요! ☀️";
            case A -> "따뜻한 마음으로 누군가에게 먼저 다가가보는 하루 어때요? 💝";
            case N -> "괜찮아요, 천천히 해도 돼요. 오늘 하루도 충분히 잘하고 있어요! 🌱";
        };
    }
}