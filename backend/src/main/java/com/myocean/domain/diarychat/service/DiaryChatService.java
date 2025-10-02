package com.myocean.domain.diarychat.service;

import com.myocean.domain.diary.entity.Diary;
import com.myocean.domain.diary.repository.DiaryRepository;
import com.myocean.domain.diarychat.dto.DiaryChatRequest;
import com.myocean.domain.diarychat.dto.DiaryChatResponse;
import com.myocean.domain.diarychat.entity.DiaryChatMessage;
import com.myocean.domain.diarychat.repository.DiaryChatRepository;
import com.myocean.domain.user.entity.Actor;
import com.myocean.domain.user.enums.ActorKind;
import com.myocean.domain.user.repository.ActorRepository;
import com.myocean.global.ai.AiClientService;
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
public class DiaryChatService {

    private final DiaryChatRepository diaryChatRepository;
    private final DiaryRepository diaryRepository;
    private final ActorRepository actorRepository;
    private final AiClientService aiClientService;

    @Transactional
    public DiaryChatResponse sendMessage(Integer userId, DiaryChatRequest request) {
        try {
            // 1. 다이어리 권한 확인
            Diary diary = diaryRepository.findByIdAndUserIdAndNotDeleted(request.getDiaryId(), userId)
                    .orElseThrow(() -> new RuntimeException("다이어리를 찾을 수 없습니다"));

            // 2. 사용자 메시지 저장
            Actor userActor = findOrCreateUserActor(userId);
            DiaryChatMessage userMessage = saveChatMessage(request.getDiaryId(), userActor.getId(), request.getMessage());

            // 3. AI 서버로 다이어리 기반 채팅 요청
            log.info("AI 서버로 다이어리 채팅 요청 - userId: {}, diaryId: {}, message: {}", 
                    userId, request.getDiaryId(), request.getMessage());
            Map<String, Object> aiResponse = aiClientService.chatWithAi(userId, request.getMessage(), "diary", request.getDiaryId());

            // 4. AI 응답 처리
            if (!(Boolean) aiResponse.get("success")) {
                // AI 실패시 에러 메시지 저장
                String errorMessage = (String) aiResponse.get("message");
                Actor personaActor = findOrCreatePersonaActor(userId);
                DiaryChatMessage errorMsg = saveChatMessage(request.getDiaryId(), personaActor.getId(), errorMessage);
                return DiaryChatResponse.from(errorMsg);
            }

            // 5. AI 성공 응답 파싱 및 저장
            String finalResponse = (String) aiResponse.get("message");
            if (finalResponse == null || finalResponse.trim().isEmpty()) {
                finalResponse = "죄송합니다. 다이어리 기반 AI 응답을 받지 못했습니다.";
            }

            Actor personaActor = findOrCreatePersonaActor(userId);
            DiaryChatMessage aiMessage = saveChatMessage(request.getDiaryId(), personaActor.getId(), finalResponse);

            // TODO: agent_responses 파싱해서 5개 페르소나별 메시지 저장
            parseAndSaveAgentResponses(userId, request.getDiaryId(), aiResponse);

            return DiaryChatResponse.from(aiMessage);

        } catch (Exception e) {
            log.error("DiaryChat 메시지 전송 실패 - userId: {}, diaryId: {}, error: {}", 
                    userId, request.getDiaryId(), e.getMessage(), e);
            throw new GeneralException(ErrorStatus.DIARY_CHAT_SEND_FAILED, e);
        }
    }

    public Page<DiaryChatResponse> getChatHistory(Integer userId, Integer diaryId, Pageable pageable) {
        // 다이어리 권한 확인
        diaryRepository.findByIdAndUserIdAndNotDeleted(diaryId, userId)
                .orElseThrow(() -> new RuntimeException("다이어리를 찾을 수 없습니다"));

        Page<DiaryChatMessage> messages = diaryChatRepository.findByDiaryIdOrderByCreatedAtAsc(diaryId, pageable);
        return messages.map(DiaryChatResponse::from);
    }

    public Long getChatCount(Integer userId, Integer diaryId) {
        // 다이어리 권한 확인
        diaryRepository.findByIdAndUserIdAndNotDeleted(diaryId, userId)
                .orElseThrow(() -> new RuntimeException("다이어리를 찾을 수 없습니다"));

        return diaryChatRepository.countByDiaryId(diaryId);
    }

    public Page<DiaryChatResponse> getUserChatHistory(Integer userId, Pageable pageable) {
        Page<DiaryChatMessage> messages = diaryChatRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return messages.map(DiaryChatResponse::from);
    }

    private DiaryChatMessage saveChatMessage(Integer diaryId, Integer senderActorId, String message) {
        DiaryChatMessage chatMessage = DiaryChatMessage.builder()
                .diaryId(diaryId)
                .senderActorId(senderActorId)
                .message(message)
                .build();

        return diaryChatRepository.save(chatMessage);
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
        // TODO: 다이어리 내용 기반 적절한 페르소나 선택 로직
        return actorRepository.findByKindAndUserId(ActorKind.PERSONA, userId)
                .orElseGet(() -> {
                    Actor personaActor = Actor.builder()
                            .kind(ActorKind.PERSONA)
                            .userId(userId)
                            .build();
                    return actorRepository.save(personaActor);
                });
    }

    private Actor findOrCreatePersonaActorByBigCode(Integer userId, com.myocean.domain.survey.enums.BigCode bigCode) {
        // 간단히 BigCode별로 구분되는 PERSONA Actor 생성/조회
        // 실제로는 더 복잡한 로직이 필요할 수 있지만, 일단 기본 PERSONA Actor 사용
        return findOrCreatePersonaActor(userId);
    }

    private void parseAndSaveAgentResponses(Integer userId, Integer diaryId, Map<String, Object> aiResponse) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, String> agentResponses = (Map<String, String>) aiResponse.get("agent_responses");

            if (agentResponses == null || agentResponses.isEmpty()) {
                log.debug("agent_responses가 비어있습니다 - userId: {}, diaryId: {}", userId, diaryId);
                return;
            }

            for (Map.Entry<String, String> entry : agentResponses.entrySet()) {
                String agentName = entry.getKey(); // "Extraversion", "Agreeableness", etc.
                String response = entry.getValue();

                if (response == null || response.trim().isEmpty()) {
                    log.debug("빈 응답 스킵 - agentName: {}, userId: {}, diaryId: {}", agentName, userId, diaryId);
                    continue;
                }

                // BigCode 매핑
                com.myocean.domain.survey.enums.BigCode bigCode =
                    com.myocean.global.ai.util.Big5AgentMapper.mapAgentNameToBigCode(agentName);

                if (bigCode == null) {
                    log.warn("알 수 없는 Agent 이름: {} - userId: {}, diaryId: {}", agentName, userId, diaryId);
                    continue;
                }

                // 해당 성향의 PERSONA Actor 찾거나 생성
                Actor personaActor = findOrCreatePersonaActorByBigCode(userId, bigCode);

                // 메시지 저장
                saveChatMessage(diaryId, personaActor.getId(), response);

                log.debug("Diary Agent 응답 저장 완료 - agentName: {}, bigCode: {}, userId: {}, diaryId: {}",
                        agentName, bigCode, userId, diaryId);
            }

            log.info("Diary Agent responses 파싱 및 저장 완료 - userId: {}, diaryId: {}, 저장된 응답 수: {}",
                    userId, diaryId, agentResponses.size());

        } catch (Exception e) {
            log.error("Diary Agent responses 파싱 중 오류 - userId: {}, diaryId: {}, error: {}",
                    userId, diaryId, e.getMessage(), e);
        }
    }
}