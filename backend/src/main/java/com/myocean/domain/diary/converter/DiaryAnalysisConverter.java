package com.myocean.domain.diary.converter;

import com.myocean.domain.diary.constants.OceanConstants;
import com.myocean.domain.diary.dto.response.DiaryAnalysisResponse;
import com.myocean.domain.diary.entity.DiaryAnalysisMessage;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class DiaryAnalysisConverter {

    // DiaryAnalysisMessage Entity → OceanMessage DTO 변환
    public static DiaryAnalysisResponse.OceanMessage toOceanMessage(DiaryAnalysisMessage message) {
        if (message == null || message.getSenderActor() == null) {
            log.error("DiaryAnalysisMessage 또는 SenderActor가 null입니다");
            throw new GeneralException(ErrorStatus.DIARY_ANALYSIS_MESSAGE_INVALID);
        }

        Integer actorId = message.getSenderActor().getId();
        OceanConstants.OceanInfo oceanInfo = OceanConstants.ACTOR_ID_TO_OCEAN_INFO.get(actorId);

        if (oceanInfo == null) {
            log.warn("Unknown actor ID: {} - OCEAN 정보를 찾을 수 없습니다", actorId);
        }

        String personality = oceanInfo != null ? oceanInfo.getEnglishName() : "UNKNOWN";
        String personalityName = oceanInfo != null ? oceanInfo.getKoreanName() : "알 수 없음";

        return DiaryAnalysisResponse.OceanMessage.builder()
                .id(message.getId())
                .personality(personality)
                .personalityName(personalityName)
                .message(message.getMessage())
                .messageOrder(message.getMessageOrder())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
