package com.myocean.domain.mychat.service;

import com.myocean.domain.mychat.dto.MyChatRequest;
import com.myocean.domain.mychat.dto.MyChatResponse;
import com.myocean.domain.mychat.entity.MyChatMessage;
import com.myocean.domain.mychat.repository.MyChatRepository;
import com.myocean.domain.mychat.converter.MyChatConverter;
import com.myocean.domain.user.entity.Actor;
import com.myocean.domain.user.enums.ActorKind;
import com.myocean.domain.user.repository.ActorRepository;
import com.myocean.global.ai.AiClientService;
import com.myocean.global.enums.BigCode;
import com.myocean.global.openai.chatanalysis.service.ChatAnalysisRefinementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
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
    private final ChatAnalysisRefinementService chatAnalysisRefinementService;

    @Transactional
    public MyChatResponse sendMessage(Integer userId, MyChatRequest request) {
        // 1. 사용자 메시지 저장 (sender_actor_id = user_id, 읽음 처리)
        MyChatMessage userMessage = saveChatMessage(userId, userId, request.getMessage(), true);

        // 2. AI 분석 플로우 시작 (실패해도 사용자 메시지는 저장됨)
        try {
            log.info("AI 채팅 분석 시작 - userId: {}, message: {}", userId, request.getMessage());

            // AI 서버로 분석 요청
            Map<String, Object> analysisResult = analyzeChatMessage(userId, userMessage.getId(), request.getMessage());

            // OpenAI로 분석 결과 다듬기 (채팅용 프롬프트 사용)
            Map<String, Object> refinedResult = refineChatAnalysisResult(request.getMessage(), analysisResult);

            // 분석 결과에서 상위 3개 성격의 메시지 저장
            saveTopThreePersonaResponses(userId, userMessage.getId(), refinedResult);

            log.info("AI 채팅 분석 및 저장 완료 - userId: {}, messageId: {}", userId, userMessage.getId());

        } catch (Exception e) {
            log.error("AI 채팅 분석 실패하지만 사용자 메시지는 저장됨 - userId: {}, messageId: {}, error: {}",
                    userId, userMessage.getId(), e.getMessage());
        }

        // 3. 사용자 메시지 정보 응답 (분석 성공/실패 관계없이)
        return MyChatConverter.toResponse(userMessage);
    }

    public Page<MyChatResponse> getChatHistory(Integer userId, Pageable pageable) {
        Page<MyChatMessage> messages = myChatRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return messages.map(MyChatConverter::toResponse);
    }

    public Long getChatCount(Integer userId) {
        return myChatRepository.countByUserId(userId);
    }

    /**
     * 안읽은 메시지 조회 및 자동 읽음 처리 (폴링용)
     */
    @Transactional
    public List<MyChatResponse> getUnreadMessages(Integer userId) {
        try {
            log.debug("안읽은 메시지 조회 - userId: {}", userId);

            List<MyChatMessage> unreadMessages = myChatRepository.findByUserIdAndIsReadFalseOrderByCreatedAtAsc(userId);

            // 안읽은 메시지가 있으면 자동으로 읽음 처리
            if (!unreadMessages.isEmpty()) {
                List<Long> messageIds = unreadMessages.stream()
                        .map(MyChatMessage::getId)
                        .collect(java.util.stream.Collectors.toList());

                myChatRepository.updateIsReadByIds(messageIds);
                log.debug("안읽은 메시지 자동 읽음 처리 완료 - userId: {}, count: {}", userId, messageIds.size());
            }

            log.debug("안읽은 메시지 조회 완료 - userId: {}, count: {}", userId, unreadMessages.size());
            return unreadMessages.stream()
                    .map(MyChatConverter::toResponse)
                    .collect(java.util.stream.Collectors.toList());

        } catch (Exception e) {
            log.error("안읽은 메시지 조회 실패 - userId: {}, error: {}", userId, e.getMessage(), e);
            return java.util.Collections.emptyList();
        }
    }

    /**
     * 안읽은 메시지 개수 조회
     */
    public Long getUnreadCount(Integer userId) {
        try {
            return myChatRepository.countUnreadByUserId(userId);
        } catch (Exception e) {
            log.error("안읽은 메시지 개수 조회 실패 - userId: {}, error: {}", userId, e.getMessage(), e);
            return 0L;
        }
    }


    private MyChatMessage saveChatMessage(Integer userId, Integer senderActorId, String message, Boolean isRead) {
        MyChatMessage chatMessage = MyChatMessage.builder()
                .userId(userId)
                .senderActorId(senderActorId)
                .message(message)
                .isRead(isRead)
                .build();

        return myChatRepository.save(chatMessage);
    }

    // 기존 메서드 호환성 유지 (기본값: false)
    private MyChatMessage saveChatMessage(Integer userId, Integer senderActorId, String message) {
        return saveChatMessage(userId, senderActorId, message, false);
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

    private Actor findOrCreatePersonaActorByBigCode(Integer userId, BigCode bigCode) {
        // 간단히 BigCode별로 구분되는 PERSONA Actor 생성/조회
        // 실제로는 더 복잡한 로직이 필요할 수 있지만, 일단 기본 PERSONA Actor 사용
        return findOrCreatePersonaActor(userId);
    }

    public Map<String, Object> analyzeChatMessage(Integer userId, Long messageId, String message) {
        try {
            log.info("AI 서버로 채팅 분석 요청 - userId: {}, messageId: {}", userId, messageId);

            // AiClientService의 새로운 메서드 사용 (추후 구현 필요)
            Map<String, Object> analysisResult = aiClientService.analyzeChatMessage(userId, messageId, message);
            return analysisResult;

        } catch (Exception e) {
            log.error("채팅 메시지 분석 실패 - userId: {}, messageId: {}, error: {}", userId, messageId, e.getMessage(), e);
            throw new RuntimeException("채팅 메시지 분석 중 오류가 발생했습니다.", e);
        }
    }

    private Map<String, Object> refineChatAnalysisResult(String message, Map<String, Object> analysisResult) {
        try {
            log.info("OpenAI로 채팅 분석 결과 다듬기 시작");

            // ChatAnalysisRefinementService를 사용해서 결과 다듬기
            return chatAnalysisRefinementService.refineChatAnalysisResult(message, analysisResult);

        } catch (Exception e) {
            log.error("채팅 분석 결과 다듬기 실패 - error: {}", e.getMessage(), e);
            return analysisResult; // 실패시 원본 반환
        }
    }

    /**
     * 상위 3개 성격의 응답을 저장 (고정 Actor ID 사용)
     */
    private void saveTopThreePersonaResponses(Integer userId, Long messageId, Map<String, Object> refinedResult) {
        try {
            log.info("상위 3개 성격 응답 저장 - userId: {}, messageId: {}", userId, messageId);

            // 래핑된 응답에서 data 추출
            Map<String, Object> actualData = refinedResult;
            if (refinedResult.containsKey("data") && refinedResult.get("data") instanceof Map) {
                actualData = (Map<String, Object>) refinedResult.get("data");
                log.debug("data 필드에서 추출됨");
            }

            // OpenAI에서 이미 상위 3개만 응답을 보내주므로 agent_responses 직접 사용
            Map<String, String> agentResponses = (Map<String, String>) actualData.get("agent_responses");

            if (agentResponses == null || agentResponses.isEmpty()) {
                log.warn("agent_responses가 없음 - userId: {}, actualData keys: {}", userId, actualData.keySet());
                return;
            }

            // 각 성격별로 메시지 저장 (고정 Actor ID 사용)
            for (Map.Entry<String, String> entry : agentResponses.entrySet()) {
                String personalityName = entry.getKey();
                String response = entry.getValue();

                if (response != null && !response.trim().isEmpty()) {
                    // 성격별 고정 Actor ID 매핑
                    Integer actorId = getPersonalityActorId(personalityName);

                    if (actorId != null) {
                        // AI 메시지 저장 (안읽음으로 설정)
                        saveChatMessage(userId, actorId, response, false);

                        log.debug("성격 응답 저장 - personality: {}, actorId: {}, userId: {}",
                                personalityName, actorId, userId);
                    } else {
                        log.warn("알 수 없는 성격 이름: {} - userId: {}", personalityName, userId);
                    }
                }
            }

            log.info("상위 3개 성격 응답 저장 완료 - userId: {}, 저장된 응답 수: {}", userId, agentResponses.size());

        } catch (Exception e) {
            log.error("상위 성격 응답 저장 실패 - userId: {}, error: {}", userId, e.getMessage(), e);
        }
    }

    /**
     * 성격 이름을 고정 Actor ID로 매핑
     * O=1, C=2, E=3, A=4, N=5
     */
    private Integer getPersonalityActorId(String personalityName) {
        if (personalityName == null) {
            return null;
        }

        String normalized = personalityName.toLowerCase();
        switch (normalized) {
            case "openness":
                return 1;  // O
            case "conscientiousness":
                return 2;  // C
            case "extraversion":
                return 3;  // E
            case "agreeableness":
                return 4;  // A
            case "neuroticism":
                return 5;  // N
            default:
                log.warn("알 수 없는 성격 이름: {}", personalityName);
                return null;
        }
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
