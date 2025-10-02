package com.myocean.domain.survey.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyAnswerRequest {

    private Integer surveyId;
    private Integer value;
}