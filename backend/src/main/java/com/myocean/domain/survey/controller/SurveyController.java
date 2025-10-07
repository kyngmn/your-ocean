package com.myocean.domain.survey.controller;

import com.myocean.domain.survey.dto.request.SurveyCompleteRequest;
import com.myocean.domain.survey.dto.response.SurveyListResponse;
import com.myocean.domain.survey.service.SurveyAnswerService;
import com.myocean.domain.survey.service.SurveyService;
import com.myocean.global.security.userdetails.CustomUserDetails;
import com.myocean.global.security.annotation.LoginMember;
import com.myocean.response.ApiResponse;
import com.myocean.response.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Survey", description = "설문조사 API")
@RestController
@RequestMapping("/api/v1/surveys")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;
    private final SurveyAnswerService surveyAnswerService;

    @Operation(summary = "설문조사 문항 조회", description = "5문항씩 설문조사 문항을 조회합니다.")
    @GetMapping
    public ApiResponse<SurveyListResponse> getSurveys(
            @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
            @RequestParam(defaultValue = "1") int page) {
        SurveyListResponse response = surveyService.getSurveys(page - 1);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "설문조사 응답 제출", description = "120문항의 설문조사 응답을 제출합니다.")
    @PostMapping("/responses")
    public ApiResponse<Void> submitSurveyResponses(
            @Valid @RequestBody SurveyCompleteRequest request,
            @LoginMember CustomUserDetails userDetails
    ) {
        Integer userId = userDetails.getUserId();
        surveyAnswerService.completeSurvey(userId, request);
        return ApiResponse.onSuccess(SuccessStatus.SURVEY_COMPLETE, null);
    }
}