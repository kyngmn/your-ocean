package com.myocean.domain.diary.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "다이어리 분석 결과 응답")
public class DiaryAnalysisResponse {

    @Schema(description = "다이어리 ID", example = "1")
    private Integer diaryId;

    @Schema(description = "OCEAN 페르소나 대화 메시지들")
    private List<OceanMessage> oceanMessages;

    @Schema(description = "분석 요약 정보")
    private AnalysisSummary summary;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "OCEAN 페르소나 메시지")
    public static class OceanMessage {

        @Schema(description = "메시지 ID", example = "1")
        private Long id;

        @Schema(description = "성격 요소", example = "Openness")
        private String personality;

        @Schema(description = "성격 요소 한국어명", example = "개방성")
        private String personalityName;

        @Schema(description = "페르소나 대화 메시지", example = "새로운 직장이라니! 정말 흥미로운 기회야...")
        private String message;

        @Schema(description = "메시지 순서", example = "1")
        private Integer messageOrder;

        @Schema(description = "생성일시")
        private LocalDateTime createdAt;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "분석 요약 정보")
    public static class AnalysisSummary {

        @Schema(description = "Big5 성격 점수", example = "{\"Extraversion\": 0.7, \"Agreeableness\": 0.6}")
        private Map<String, Double> big5Scores;

        @Schema(description = "주요 도메인 분류", example = "NEUROTICISM")
        private String domainClassification;

        @Schema(description = "최종 결론", example = "성공적인 새 직장 적응을 위해서는...")
        private String finalConclusion;

        @Schema(description = "핵심 키워드", example = "[\"적응\", \"불안\", \"새출발\"]")
        private List<String> keywords;
    }
}