package com.myocean.domain.diary.converter;

import com.myocean.domain.diary.constants.OceanConstants;
import com.myocean.domain.diary.dto.response.DiaryAnalysisResponse;
import com.myocean.domain.diary.entity.DiaryAnalysisMessage;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DiaryAnalysisConverter {

    // DiaryAnalysisMessage Entity → OceanMessage DTO 변환
    public static DiaryAnalysisResponse.OceanMessage toOceanMessage(DiaryAnalysisMessage message) {
        OceanConstants.OceanInfo oceanInfo = OceanConstants.ACTOR_ID_TO_OCEAN_INFO.get(message.getSenderActor().getId());
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
