package com.myocean.domain.survey.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "설문 문항 응답")
public class SurveyResponse {

    @Schema(description = "설문 ID", example = "1")
    private Short id;

    @Schema(description = "설문 문항 내용", example = "나는 대화를 먼저 시작하는 편이다.")
    private String questionText;
}