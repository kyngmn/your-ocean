package com.myocean.domain.survey.controller;

import com.myocean.domain.survey.dto.request.SurveyCompleteRequest;
import com.myocean.domain.survey.service.SurveyResponseService;
import com.myocean.global.auth.CustomUserDetails;
import com.myocean.global.auth.LoginMember;
import com.myocean.response.ApiResponse;
import com.myocean.response.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "SurveyResponse", description = "Survey Response API")
@RestController
@RequestMapping("/api/v1/survey-responses")
@RequiredArgsConstructor
public class SurveyResponseController {

    private final SurveyResponseService surveyResponseService;

    @Operation(summary = "설문조사 완료", description = "120문항의 설문조사 응답을 제출")
    @PostMapping("/complete")
    public ApiResponse<Void> completeSurvey(
            @Valid @RequestBody SurveyCompleteRequest request,
            @LoginMember CustomUserDetails userDetails) {

        Integer userId = userDetails.getUserId();
        surveyResponseService.completeSurvey(userId, request);
        return ApiResponse.onSuccess(SuccessStatus.SURVEY_COMPLETE, null);
    }
}

