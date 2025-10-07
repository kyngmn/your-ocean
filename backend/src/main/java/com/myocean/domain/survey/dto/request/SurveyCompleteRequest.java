package com.myocean.domain.survey.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "설문조사 완료 요청")
public class SurveyCompleteRequest {

    @NotEmpty(message = "설문 응답은 최소 1개 이상이어야 합니다.")
    @Size(min = 120, max = 120, message = "설문 응답은 정확히 120개여야 합니다.")
    @Valid
    @Schema(description = "120개의 설문 응답 목록")
    private List<SurveyAnswerRequest> responses;
}