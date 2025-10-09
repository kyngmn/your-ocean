package com.myocean.domain.mychat.service;

import com.myocean.domain.mychat.dto.request.MyChatRequest;
import com.myocean.domain.mychat.dto.response.MyChatResponse;
import com.myocean.domain.mychat.entity.MyChatMessage;
import com.myocean.domain.mychat.repository.MyChatRepository;
import com.myocean.domain.mychat.converter.MyChatConverter;
import com.myocean.domain.user.entity.Actor;
import com.myocean.domain.user.enums.ActorKind;
import com.myocean.domain.user.repository.ActorRepository;
import com.myocean.domain.user.repository.UserRepository;
import com.myocean.global.enums.AnalysisStatus;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MyChatService {

    private final MyChatRepository myChatRepository;
    private final ActorRepository actorRepository;
    private final UserRepository userRepository;
    private final MyChatAsyncService asyncService;
    private final MyChatMessageService messageService;

    public SseEmitter createSseEmitter(Integer userId) {
        return messageService.createSseEmitter(userId);
    }

    @Transactional
    public MyChatResponse sendMessage(Integer userId, MyChatRequest request) {
        log.info("[SYNC] sendMessage 시작 - userId: {}", userId);

        // 사용자의 USER 타입 Actor 조회
        Actor userActor = actorRepository.findByKindAndUser_Id(ActorKind.USER, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR));

        // 사용자 메시지 저장 (sender_actor = USER 타입 Actor, 읽음 처리, 분석 상태 PROCESSING)
        MyChatMessage userMessage = MyChatMessage.builder()
                .user(userRepository.getReferenceById(userId))
                .senderActor(userActor)
                .message(request.getMessage())
                .isRead(true)
                .analysisStatus(AnalysisStatus.PROCESSING)
                .build();

        MyChatMessage savedUserMessage = myChatRepository.save(userMessage);

        // 비동기 AI 분석 시작
        asyncService.asyncAnalyzeMyChat(userId, savedUserMessage.getId(), savedUserMessage.getMessage());

        log.info("[SYNC] sendMessage 완료 - messageId: {}", savedUserMessage.getId());
        return MyChatConverter.toResponse(savedUserMessage);
    }

    public Page<MyChatResponse> getChatHistory(Integer userId, Pageable pageable) {
        Page<MyChatMessage> messages = myChatRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return messages.map(MyChatConverter::toResponse);
    }

    public Long getChatCount(Integer userId) {
        return myChatRepository.countByUserId(userId);
    }
}
