package com.myocean.domain.survey.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "설문 문항 목록 응답")
public class SurveyListResponse {

    @Schema(description = "설문 문항 목록 (페이지당 5개)")
    private List<SurveyResponse> surveys;

    @Schema(description = "현재 페이지 번호 (1부터 시작)", example = "1")
    private Integer currentPage;

    @Schema(description = "전체 페이지 수", example = "24")
    private Integer totalPages;

    @Schema(description = "다음 페이지 존재 여부", example = "true")
    private Boolean hasNext;
}