package com.myocean.domain.mychat.service;

import com.myocean.domain.mychat.converter.MyChatConverter;
import com.myocean.domain.mychat.dto.response.MyChatResponse;
import com.myocean.domain.mychat.entity.MyChatMessage;
import com.myocean.domain.mychat.repository.MyChatRepository;
import com.myocean.domain.user.repository.ActorRepository;
import com.myocean.domain.user.repository.UserRepository;
import com.myocean.global.enums.BigCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 채팅 메시지 저장/조회/SSE 전송 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MyChatMessageService {

    private final MyChatRepository myChatRepository;
    private final ActorRepository actorRepository;
    private final UserRepository userRepository;

    // SSE Emitter 관리 (userId -> SseEmitter)
    private final Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();
    private static final Long SSE_TIMEOUT = 60 * 60 * 1000L; // 1시간

    /**
     * SSE Emitter 생성
     */
    public SseEmitter createSseEmitter(Integer userId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        // 기존 연결이 있으면 제거
        if (emitters.containsKey(userId)) {
            SseEmitter oldEmitter = emitters.get(userId);
            oldEmitter.complete();
            emitters.remove(userId);
            log.info("기존 SSE 연결 종료 - userId: {}", userId);
        }

        // 새 연결 등록
        emitters.put(userId, emitter);
        log.info("SSE 연결 생성 - userId: {}", userId);

        // 연결 종료 처리
        emitter.onCompletion(() -> {
            emitters.remove(userId);
            log.info("SSE 연결 완료 - userId: {}", userId);
        });

        emitter.onTimeout(() -> {
            emitters.remove(userId);
            log.info("SSE 연결 타임아웃 - userId: {}", userId);
        });

        emitter.onError((e) -> {
            emitters.remove(userId);
            log.error("SSE 연결 에러 - userId: {}, error: {}", userId, e.getMessage());
        });

        // 초기 연결 확인 메시지 전송
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("Connected"));
        } catch (Exception e) {
            log.error("SSE 초기 메시지 전송 실패 - userId: {}", userId);
        }
        return emitter;
    }

    /**
     * 상위 3개 성격 응답 저장
     */
    @Transactional
    public List<MyChatMessage> saveTopThreePersonaResponses(Integer userId, Map<String, String> agentResponses) {
        List<MyChatMessage> savedMessages = new ArrayList<>();

        log.info("상위 3개 성격 응답 저장 - userId: {}", userId);

        for (Map.Entry<String, String> entry : agentResponses.entrySet()) {
            String personalityName = entry.getKey();
            String response = entry.getValue();

            if (response == null || response.trim().isEmpty()) {
                continue;
            }

            Long actorId = BigCode.getActorIdByEnglishName(personalityName);

            if (actorId != null) {
                // AI 메시지 저장 (안읽음으로 설정)
                MyChatMessage aiMessage = MyChatMessage.builder()
                        .user(userRepository.getReferenceById(userId))
                        .senderActor(actorRepository.getReferenceById(actorId))
                        .message(response)
                        .isRead(false)
                        .build();

                MyChatMessage savedMessage = myChatRepository.save(aiMessage);
                savedMessages.add(savedMessage);

                log.debug("성격 응답 저장 - personality: {}, actorId: {}", personalityName, actorId);
            } else {
                log.warn("알 수 없는 성격 이름: {} - userId: {}", personalityName, userId);
            }
        }

        log.info("상위 3개 성격 응답 저장 완료 - userId: {}, 저장된 메시지 수: {}", userId, savedMessages.size());

        return savedMessages;
    }

    /**
     * 저장된 메시지들을 SSE로 스트리밍 (1-5초 랜덤 간격)
     */
    @Transactional
    public void streamMessages(Integer userId, List<MyChatMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            log.debug("전송할 메시지 없음 - userId: {}", userId);
            return;
        }

        log.info("SSE로 메시지 스트리밍 시작 - userId: {}, 메시지 수: {}", userId, messages.size());

        java.util.Random random = new java.util.Random();

        for (int i = 0; i < messages.size(); i++) {
            MyChatMessage message = messages.get(i);
            sendMessageViaSSE(userId, message);

            // 마지막 메시지가 아니면 1-5초 랜덤 대기
            if (i < messages.size() - 1) {
                try {
                    int delaySeconds = random.nextInt(5) + 1; // 1~5초 랜덤
                    log.debug("다음 메시지까지 {}초 대기 - userId: {}", delaySeconds, userId);
                    Thread.sleep(delaySeconds * 1000L);
                } catch (InterruptedException e) {
                    log.warn("SSE 스트리밍 중 인터럽트 발생 - userId: {}", userId);
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        log.info("SSE로 메시지 스트리밍 완료 - userId: {}, 전송된 메시지 수: {}", userId, messages.size());
    }

    /**
     * 단일 메시지를 SSE로 전송
     */
    private void sendMessageViaSSE(Integer userId, MyChatMessage message) {
        SseEmitter emitter = emitters.get(userId);

        if (emitter == null) {
            log.debug("SSE 연결 없음 - userId: {}, 메시지는 DB에 저장됨", userId);
            return;
        }

        try {
            MyChatResponse chatResponse = MyChatConverter.toResponse(message);

            // ApiResponse로 감싸기
            com.myocean.response.ApiResponse<MyChatResponse> apiResponse =
                com.myocean.response.ApiResponse.onSuccess(chatResponse);

            emitter.send(SseEmitter.event()
                    .name("message")
                    .data(apiResponse));

            log.debug("SSE로 메시지 전송 완료 - userId: {}, messageId: {}", userId, message.getId());

            // SSE로 전송했으므로 읽음 처리
            message.setIsRead(true);
            myChatRepository.save(message);

        } catch (Exception e) {
            log.error("SSE 메시지 전송 실패 - userId: {}, messageId: {}, error: {}",
                    userId, message.getId(), e.getMessage());
            emitters.remove(userId);
        }
    }
}
