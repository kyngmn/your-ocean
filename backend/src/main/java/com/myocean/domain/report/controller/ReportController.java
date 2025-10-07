package com.myocean.domain.report.controller;

import com.myocean.domain.report.dto.response.ReportResponse;
import com.myocean.domain.report.service.ReportService;
import com.myocean.global.auth.LoginMember;
import com.myocean.global.auth.CustomUserDetails;
import com.myocean.response.ApiResponse;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Report", description = "Report API")
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Get SELF report", description = "Get user SELF report")
    @GetMapping("/self")
    public ApiResponse<ReportResponse> getSelfReport(
            @LoginMember CustomUserDetails userDetails
    ) {
        Integer userId = extractUserId(userDetails);
        ReportResponse response = reportService.getSelfReport(userId);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "Get FINAL report", description = "Get user FINAL report")
    @GetMapping("/final")
    public ApiResponse<ReportResponse> getFinalReport(
            @LoginMember CustomUserDetails userDetails
    ) {
        Integer userId = extractUserId(userDetails);
        ReportResponse response = reportService.getFinalReport(userId);
        return ApiResponse.onSuccess(response);
    }

    private Integer extractUserId(CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getUser() == null) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }
        Integer userId = userDetails.getUserId();
        return userId;
    }
}