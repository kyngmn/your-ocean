package com.myocean.domain.survey.dto.converter;

import com.myocean.domain.survey.dto.response.SurveyListResponse;
import com.myocean.domain.survey.dto.response.SurveyResponse;
import com.myocean.domain.survey.entity.Survey;
import org.springframework.data.domain.Page;

import java.util.List;

public class SurveyConverter {

    public static SurveyResponse toSurveyResponse(Survey survey) {
        return SurveyResponse.builder()
                .id(survey.getId())
                .questionText(survey.getQuestionText())
                .build();
    }

    public static SurveyListResponse toSurveyListResponse(Page<Survey> surveyPage, List<SurveyResponse> surveyResponses) {
        return SurveyListResponse.builder()
                .surveys(surveyResponses)
                .currentPage(surveyPage.getNumber() + 1)  // 0-based를 1-based로 변환
                .totalPages(surveyPage.getTotalPages())
                .hasNext(surveyPage.hasNext())
                .build();
    }
}