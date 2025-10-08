package com.myocean.domain.diary.service;

import com.myocean.domain.diary.constants.OceanConstants;
import com.myocean.domain.diary.entity.DiaryAnalysisMessage;
import com.myocean.domain.diary.repository.DiaryAnalysisMessageRepository;
import com.myocean.domain.diary.repository.DiaryRepository;
import com.myocean.domain.user.repository.ActorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 다이어리 분석 메시지 저장/조회 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DiaryAnalysisMessageService {

    private final DiaryAnalysisMessageRepository diaryAnalysisMessageRepository;
    private final DiaryRepository diaryRepository;
    private final ActorRepository actorRepository;

    /**
     * OCEAN 모델 기반으로 5개 분석 메시지를 diary_analysis_messages에 저장
     */
    @Transactional
    public void saveOceanAnalysisMessages(Integer userId, Integer diaryId, Map<String, Object> agentResponses) {
        log.info("OCEAN 분석 메시지 저장 - userId: {}, diaryId: {}", userId, diaryId);

        int messageOrder = 1;
        for (Map.Entry<String, Integer> entry : OceanConstants.OCEAN_TYPE_TO_ACTOR_ID.entrySet()) {
            String oceanType = entry.getKey();
            Integer actorId = entry.getValue();

            String message = (String) agentResponses.get(oceanType);
            if (message != null) {
                saveChatMessage(diaryId, actorId, message, messageOrder++);
            }
        }
        log.info("OCEAN 분석 메시지 저장 완료 - diaryId: {}", diaryId);
    }

    /**
     * 개별 채팅 메시지 저장
     */
    @Transactional
    public void saveChatMessage(Integer diaryId, Integer actorId, String message, Integer messageOrder) {
        DiaryAnalysisMessage chatMessage = DiaryAnalysisMessage.builder()
                .diary(diaryRepository.getReferenceById(diaryId))  // 프록시 사용
                .senderActor(actorRepository.getReferenceById(actorId))  // 프록시 사용
                .message(message)
                .messageOrder(messageOrder)
                .build();

        diaryAnalysisMessageRepository.save(chatMessage);
        log.debug("채팅 메시지 저장 - diaryId: {}, actorId: {}, order: {}, message: {}",
                diaryId, actorId, messageOrder, message.substring(0, Math.min(50, message.length())));
    }

    /**
     * 저장된 OCEAN 분석 메시지들 조회
     */
    public List<DiaryAnalysisMessage> getStoredAnalysisMessages(Integer userId, Integer diaryId) {
        return diaryAnalysisMessageRepository.findByDiaryIdOrderByCreatedAtAsc(diaryId);
    }
}
