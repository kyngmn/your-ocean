package com.myocean.domain.diary.converter;

import com.myocean.global.enums.BigCode;
import com.myocean.domain.diary.dto.response.DiaryAnalysisResponse;
import com.myocean.domain.diary.entity.DiaryAnalysisMessage;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@UtilityClass
@Slf4j
public class DiaryAnalysisConverter {

    /**
     * DiaryAnalysisMessage 리스트를 DiaryAnalysisResponse로 변환
     */
    public static DiaryAnalysisResponse toAnalysisResponse(
            Integer diaryId,
            List<DiaryAnalysisMessage> analysisMessages,
            DiaryAnalysisResponse.AnalysisSummary summary,
            String status
    ) {
        List<DiaryAnalysisResponse.OceanMessage> oceanMessages = analysisMessages.stream()
                .map(DiaryAnalysisConverter::toOceanMessage)
                .toList();

        return DiaryAnalysisResponse.builder()
                .diaryId(diaryId)
                .status(status)
                .oceanMessages(oceanMessages)
                .summary(summary)
                .build();
    }

    /**
     * DiaryAnalysisMessage Entity → OceanMessage DTO 변환
     */
    public static DiaryAnalysisResponse.OceanMessage toOceanMessage(DiaryAnalysisMessage message) {
        if (message == null || message.getSenderActor() == null) {
            log.error("DiaryAnalysisMessage 또는 SenderActor가 null입니다");
            throw new GeneralException(ErrorStatus.DIARY_ANALYSIS_MESSAGE_INVALID);
        }

        Long actorId = message.getSenderActor().getId();
        BigCode bigCode = findBigCodeByActorId(actorId);

        if (bigCode == null) {
            log.warn("Unknown actor ID: {} - OCEAN 정보를 찾을 수 없습니다", actorId);
        }

        String personality = bigCode != null ? bigCode.getEnglishName().toUpperCase() : "UNKNOWN";
        String personalityName = bigCode != null ? bigCode.getKoreanName() : "알 수 없음";

        return DiaryAnalysisResponse.OceanMessage.builder()
                .id(message.getId())
                .personality(personality)
                .personalityName(personalityName)
                .message(message.getMessage())
                .messageOrder(message.getMessageOrder())
                .createdAt(message.getCreatedAt())
                .build();
    }

    /**
     * Actor ID로 BigCode 찾기
     */
    private static BigCode findBigCodeByActorId(Long actorId) {
        for (BigCode code : BigCode.values()) {
            if (code.getActorId().equals(actorId)) {
                return code;
            }
        }
        return null;
    }
}
