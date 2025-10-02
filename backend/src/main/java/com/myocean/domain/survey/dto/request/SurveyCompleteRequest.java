package com.myocean.domain.survey.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyCompleteRequest {

    private List<SurveyAnswerRequest> responses;
}