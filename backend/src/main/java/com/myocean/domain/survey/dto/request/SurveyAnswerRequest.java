package com.myocean.domain.survey.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "설문 응답 요청")
public class SurveyAnswerRequest {

    @NotNull(message = "설문 ID는 필수입니다.")
    @Min(value = 1, message = "설문 ID는 1 이상이어야 합니다.")
    @Max(value = 120, message = "설문 ID는 120 이하여야 합니다.")
    @Schema(description = "설문 문항 ID", example = "1", minimum = "1", maximum = "120")
    private Integer surveyId;

    @NotNull(message = "응답 값은 필수입니다.")
    @Min(value = 1, message = "응답 값은 1(매우 아니다) 이상이어야 합니다.")
    @Max(value = 5, message = "응답 값은 5(매우 그렇다) 이하여야 합니다.")
    @Schema(description = "응답 값 (1: 매우 아니다, 5: 매우 그렇다)", example = "3", minimum = "1", maximum = "5")
    private Integer value;
}