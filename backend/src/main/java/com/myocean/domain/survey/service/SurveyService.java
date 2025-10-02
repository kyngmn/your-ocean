package com.myocean.domain.survey.service;

import com.myocean.domain.survey.dto.converter.SurveyConverter;
import com.myocean.domain.survey.dto.response.SurveyListResponse;
import com.myocean.domain.survey.dto.response.SurveyResponse;
import com.myocean.domain.survey.entity.Survey;
import com.myocean.domain.survey.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private static final int PAGE_SIZE = 5;

    public SurveyListResponse getSurveys(int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<Survey> surveyPage = surveyRepository.findAll(pageable);

        List<SurveyResponse> surveyResponses = surveyPage.getContent().stream()
                .map(SurveyConverter::toSurveyResponse)
                .toList();

        return SurveyConverter.toSurveyListResponse(surveyPage, surveyResponses);
    }
}