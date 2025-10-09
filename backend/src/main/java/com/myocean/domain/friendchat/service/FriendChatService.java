package com.myocean.domain.friendchat.service;

import com.myocean.domain.friendchat.dto.FriendChatRequest;
import com.myocean.domain.friendchat.dto.FriendChatResponse;
import com.myocean.domain.friendchat.entity.Friend;
import com.myocean.domain.friendchat.entity.FriendChatMessage;
import com.myocean.domain.friendchat.repository.FriendChatRepository;
import com.myocean.domain.friendchat.repository.FriendRepository;
import com.myocean.domain.user.entity.Actor;
import com.myocean.domain.user.enums.ActorKind;
import com.myocean.domain.user.repository.ActorRepository;
import com.myocean.global.ai.AiClientService;
import com.myocean.global.enums.BigCode;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FriendChatService {

    private final FriendChatRepository friendChatRepository;
    private final FriendRepository friendRepository;
    private final ActorRepository actorRepository;
    private final AiClientService aiClientService;

    @Transactional
    public FriendChatResponse sendMessage(Integer userId, FriendChatRequest request) {
        try {
            // 1. 친구 관계 권한 확인
            Friend friendship = friendRepository.findById(request.getRoomId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.FRIENDSHIP_NOT_FOUND));

            if (!friendship.getUserId().equals(userId) && !friendship.getFriendId().equals(userId)) {
                throw new GeneralException(ErrorStatus.CHAT_ACCESS_DENIED);
            }

            // 2. 사용자 메시지 저장
            Actor userActor = findOrCreateUserActor(userId);
            FriendChatMessage userMessage = saveChatMessage(request.getRoomId(), userActor.getId(), request.getMessage());

            // 3. AI 서버로 친구 페르소나 기반 채팅 요청
            log.info("AI 서버로 친구 페르소나 채팅 요청 - userId: {}, roomId: {}, message: {}", 
                    userId, request.getRoomId(), request.getMessage());
            Map<String, Object> aiResponse = aiClientService.chatWithAi(userId, request.getMessage(), "friend", request.getRoomId());

            // 4. AI 응답 처리
            if (!(Boolean) aiResponse.get("success")) {
                // AI 실패시 에러 메시지 저장
                String errorMessage = (String) aiResponse.get("message");
                Actor personaActor = findOrCreatePersonaActor(userId);
                FriendChatMessage errorMsg = saveChatMessage(request.getRoomId(), personaActor.getId(), errorMessage);
                return FriendChatResponse.from(errorMsg);
            }

            // 5. AI 성공 응답 파싱 및 저장
            String finalResponse = (String) aiResponse.get("message");
            if (finalResponse == null || finalResponse.trim().isEmpty()) {
                finalResponse = "죄송합니다. 친구 페르소나 AI 응답을 받지 못했습니다.";
            }

            Actor personaActor = findOrCreatePersonaActor(userId);
            FriendChatMessage aiMessage = saveChatMessage(request.getRoomId(), personaActor.getId(), finalResponse);

            parseAndSaveAgentResponses(userId, request.getRoomId(), aiResponse);

            return FriendChatResponse.from(aiMessage);

        } catch (Exception e) {
            log.error("FriendChat 메시지 전송 실패 - userId: {}, roomId: {}, error: {}", 
                    userId, request.getRoomId(), e.getMessage(), e);
            throw new GeneralException(ErrorStatus.CHAT_MESSAGE_SEND_FAILED, e);
        }
    }

    public Page<FriendChatResponse> getChatHistory(Integer userId, Integer roomId, Pageable pageable) {
        // 친구 관계 권한 확인
        Friend friendship = friendRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("친구 관계를 찾을 수 없습니다"));

        if (!friendship.getUserId().equals(userId) && !friendship.getFriendId().equals(userId)) {
            throw new GeneralException(ErrorStatus.CHAT_ACCESS_DENIED);
        }

        Page<FriendChatMessage> messages = friendChatRepository.findByRoomIdOrderByCreatedAtAsc(roomId, pageable);
        return messages.map(FriendChatResponse::from);
    }

    public Long getChatCount(Integer userId, Integer roomId) {
        // 친구 관계 권한 확인
        Friend friendship = friendRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("친구 관계를 찾을 수 없습니다"));

        if (!friendship.getUserId().equals(userId) && !friendship.getFriendId().equals(userId)) {
            throw new GeneralException(ErrorStatus.CHAT_ACCESS_DENIED);
        }

        return friendChatRepository.countByRoomId(roomId);
    }

    public Page<FriendChatResponse> getUserChatHistory(Integer userId, Pageable pageable) {
        Page<FriendChatMessage> messages = friendChatRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return messages.map(FriendChatResponse::from);
    }

    private FriendChatMessage saveChatMessage(Integer roomId, Long senderActorId, String message) {
        FriendChatMessage chatMessage = FriendChatMessage.builder()
                .roomId(roomId)
                .senderActorId(senderActorId)
                .message(message)
                .build();

        return friendChatRepository.save(chatMessage);
    }

    private Actor findOrCreateUserActor(Integer userId) {
        // Actor는 User 생성 시 자동 생성됨
        return actorRepository.findByKindAndUser_Id(ActorKind.USER, userId)
                .orElseThrow(() -> new RuntimeException("User Actor not found"));
    }

    private Actor findOrCreatePersonaActor(Integer userId) {
        // TODO: 친구 페르소나에 따른 적절한 페르소나 선택 로직
        // Actor는 UserPersona 생성 시 자동 생성됨
        return actorRepository.findByKindAndUser_Id(ActorKind.USER, userId)
                .orElseThrow(() -> new RuntimeException("User Actor not found"));
    }

    private Actor findOrCreatePersonaActorByBigCode(Integer userId, com.myocean.global.enums.BigCode bigCode) {
        // 간단히 BigCode별로 구분되는 PERSONA Actor 생성/조회
        // 실제로는 더 복잡한 로직이 필요할 수 있지만, 일단 기본 PERSONA Actor 사용
        return findOrCreatePersonaActor(userId);
    }

    private void parseAndSaveAgentResponses(Integer userId, Integer roomId, Map<String, Object> aiResponse) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, String> agentResponses = (Map<String, String>) aiResponse.get("agent_responses");

            if (agentResponses == null || agentResponses.isEmpty()) {
                log.debug("agent_responses가 비어있습니다 - userId: {}, roomId: {}", userId, roomId);
                return;
            }

            for (Map.Entry<String, String> entry : agentResponses.entrySet()) {
                String agentName = entry.getKey(); // "Extraversion", "Agreeableness", etc.
                String response = entry.getValue();

                if (response == null || response.trim().isEmpty()) {
                    log.debug("빈 응답 스킵 - agentName: {}, userId: {}, roomId: {}", agentName, userId, roomId);
                    continue;
                }

                // BigCode 매핑
                BigCode bigCode =
                    com.myocean.global.ai.util.Big5AgentMapper.mapAgentNameToBigCode(agentName);

                if (bigCode == null) {
                    log.warn("알 수 없는 Agent 이름: {} - userId: {}, roomId: {}", agentName, userId, roomId);
                    continue;
                }

                // 해당 성향의 PERSONA Actor 찾거나 생성
                Actor personaActor = findOrCreatePersonaActorByBigCode(userId, bigCode);

                // 메시지 저장
                saveChatMessage(roomId, personaActor.getId(), response);

                log.debug("Friend Agent 응답 저장 완료 - agentName: {}, bigCode: {}, userId: {}, roomId: {}",
                        agentName, bigCode, userId, roomId);
            }

            log.info("Friend Agent responses 파싱 및 저장 완료 - userId: {}, roomId: {}, 저장된 응답 수: {}",
                    userId, roomId, agentResponses.size());

        } catch (Exception e) {
            log.error("Friend Agent responses 파싱 중 오류 - userId: {}, roomId: {}, error: {}",
                    userId, roomId, e.getMessage(), e);
        }
    }
}