package com.myocean.domain.mychat.service;

import com.myocean.domain.mychat.dto.MyChatRequest;
import com.myocean.domain.mychat.dto.MyChatResponse;
import com.myocean.domain.mychat.entity.MyChatMessage;
import com.myocean.domain.mychat.repository.MyChatRepository;
import com.myocean.domain.user.entity.Actor;
import com.myocean.domain.user.enums.ActorKind;
import com.myocean.domain.user.repository.ActorRepository;
import com.myocean.global.ai.AiClientService;
import com.myocean.global.enums.BigCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MyChatService {

    private final MyChatRepository myChatRepository;
    private final ActorRepository actorRepository;
    private final AiClientService aiClientService;

    @Transactional
    public MyChatResponse sendMessage(Integer userId, MyChatRequest request) {
        try {
            // 1. 사용자 메시지 저장
            Actor userActor = findOrCreateUserActor(userId);
            MyChatMessage userMessage = saveChatMessage(userId, userActor.getId(), request.getMessage());

            // 2. AI 서버로 요청 전송
            log.info("AI 서버로 채팅 요청 - userId: {}, message: {}", userId, request.getMessage());
            Map<String, Object> aiResponse = aiClientService.chatWithAi(userId, request.getMessage(), "my", null);

            // 3. AI 응답 처리
            if (!(Boolean) aiResponse.get("success")) {
                // AI 실패시 에러 메시지 저장
                String errorMessage = (String) aiResponse.get("message");
                Actor personaActor = findOrCreatePersonaActor(userId);
                MyChatMessage errorMsg = saveChatMessage(userId, personaActor.getId(), errorMessage);
                return MyChatResponse.from(errorMsg);
            }

            // 4. AI 성공 응답 파싱 및 저장
            String finalResponse = (String) aiResponse.get("message");
            if (finalResponse == null || finalResponse.trim().isEmpty()) {
                finalResponse = "죄송합니다. AI 응답을 받지 못했습니다.";
            }

            Actor personaActor = findOrCreatePersonaActor(userId);
            MyChatMessage aiMessage = saveChatMessage(userId, personaActor.getId(), finalResponse);

            // TODO: agent_responses 파싱해서 5개 페르소나별 메시지 저장
            parseAndSaveAgentResponses(userId, aiResponse);

            return MyChatResponse.from(aiMessage);

        } catch (Exception e) {
            log.error("MyChat 메시지 전송 실패 - userId: {}, error: {}", userId, e.getMessage(), e);
            throw new RuntimeException("채팅 메시지 전송 중 오류가 발생했습니다.", e);
        }
    }

    public Page<MyChatResponse> getChatHistory(Integer userId, Pageable pageable) {
        Page<MyChatMessage> messages = myChatRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return messages.map(MyChatResponse::from);
    }

    public Long getChatCount(Integer userId) {
        return myChatRepository.countByUserId(userId);
    }

    private MyChatMessage saveChatMessage(Integer userId, Integer senderActorId, String message) {
        MyChatMessage chatMessage = MyChatMessage.builder()
                .userId(userId)
                .senderActorId(senderActorId)
                .message(message)
                .build();

        return myChatRepository.save(chatMessage);
    }

    private Actor findOrCreateUserActor(Integer userId) {
        return actorRepository.findByKindAndUserId(ActorKind.USER, userId)
                .orElseGet(() -> {
                    Actor userActor = Actor.builder()
                            .kind(ActorKind.USER)
                            .userId(userId)
                            .build();
                    return actorRepository.save(userActor);
                });
    }

    private Actor findOrCreatePersonaActor(Integer userId) {
        // TODO: Big5 점수에 따른 적절한 페르소나 선택 로직
        return actorRepository.findByKindAndUserId(ActorKind.PERSONA, userId)
                .orElseGet(() -> {
                    Actor personaActor = Actor.builder()
                            .kind(ActorKind.PERSONA)
                            .userId(userId)
                            .build();
                    return actorRepository.save(personaActor);
                });
    }

    private Actor findOrCreatePersonaActorByBigCode(Integer userId, com.myocean.global.enums.BigCode bigCode) {
        // 간단히 BigCode별로 구분되는 PERSONA Actor 생성/조회
        // 실제로는 더 복잡한 로직이 필요할 수 있지만, 일단 기본 PERSONA Actor 사용
        return findOrCreatePersonaActor(userId);
    }

    private void parseAndSaveAgentResponses(Integer userId, Map<String, Object> aiResponse) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, String> agentResponses = (Map<String, String>) aiResponse.get("agent_responses");

            if (agentResponses == null || agentResponses.isEmpty()) {
                log.debug("agent_responses가 비어있습니다 - userId: {}", userId);
                return;
            }

            for (Map.Entry<String, String> entry : agentResponses.entrySet()) {
                String agentName = entry.getKey(); // "Extraversion", "Agreeableness", etc.
                String response = entry.getValue();

                if (response == null || response.trim().isEmpty()) {
                    log.debug("빈 응답 스킵 - agentName: {}, userId: {}", agentName, userId);
                    continue;
                }

                // BigCode 매핑
                BigCode bigCode =
                    com.myocean.global.ai.util.Big5AgentMapper.mapAgentNameToBigCode(agentName);

                if (bigCode == null) {
                    log.warn("알 수 없는 Agent 이름: {} - userId: {}", agentName, userId);
                    continue;
                }

                // 해당 성향의 PERSONA Actor 찾거나 생성
                Actor personaActor = findOrCreatePersonaActorByBigCode(userId, bigCode);

                // 메시지 저장
                saveChatMessage(userId, personaActor.getId(), response);

                log.debug("Agent 응답 저장 완료 - agentName: {}, bigCode: {}, userId: {}",
                        agentName, bigCode, userId);
            }

            log.info("Agent responses 파싱 및 저장 완료 - userId: {}, 저장된 응답 수: {}",
                    userId, agentResponses.size());

        } catch (Exception e) {
            log.error("Agent responses 파싱 중 오류 - userId: {}, error: {}", userId, e.getMessage(), e);
        }
    }
}