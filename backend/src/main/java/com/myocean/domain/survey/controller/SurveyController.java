package com.myocean.domain.survey.controller;

import com.myocean.domain.survey.dto.response.SurveyListResponse;
import com.myocean.domain.survey.service.SurveyService;
import com.myocean.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Survey", description = "Survey API")
@RestController
@RequestMapping("/api/v1/surveys")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    @Operation(summary = "설문조사 문항 조회", description = "5문항씩 설문조사 가져오기")
    @GetMapping
    public ApiResponse<SurveyListResponse> getSurveys(
            @Parameter(description = "페이지네이션 1페이지부터 시작", example = "1")
            @RequestParam(defaultValue = "1") int page) {

        // 1-based를 0-based로 변환
        SurveyListResponse response = surveyService.getSurveys(page - 1);
        return ApiResponse.onSuccess(response);
    }
}