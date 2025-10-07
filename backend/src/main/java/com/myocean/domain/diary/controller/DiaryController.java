package com.myocean.domain.diary.controller;

import com.myocean.domain.diary.dto.request.DiaryCreateRequest;
import com.myocean.domain.diary.dto.response.DiaryCalendarResponse;
import com.myocean.domain.diary.dto.response.DiaryResponse;
import com.myocean.domain.diary.service.DiaryService;
import com.myocean.global.security.userdetails.CustomUserDetails;
import com.myocean.global.security.annotation.LoginMember;
import com.myocean.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Diary", description = "Diary API")
@RestController
@RequestMapping("/api/v1/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @Operation(summary = "Create diary", description = "Create new diary")
    @PostMapping
    public ApiResponse<DiaryResponse> createDiary(
            @Valid @RequestBody DiaryCreateRequest request,
            @LoginMember CustomUserDetails userDetails) {

        Integer userId = extractUserId(userDetails);
        DiaryResponse response = diaryService.createDiary(userId, request);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "Get diary by ID", description = "Get diary by specific diary ID")
    @GetMapping("/{diaryId}")
    public ApiResponse<DiaryResponse> getDiaryById(
            @Parameter(description = "Diary ID", example = "1") @PathVariable Integer diaryId,
            @LoginMember CustomUserDetails userDetails) {

        Integer userId = extractUserId(userDetails);
        DiaryResponse response = diaryService.getDiaryById(userId, diaryId);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "Get diary by date", description = "Get diary by specific date")
    @GetMapping("/date/{diaryDate}")
    public ApiResponse<DiaryResponse> getDiaryByDate(
            @Parameter(description = "Diary date (YYYY-MM-DD format)", example = "2024-02-15") @PathVariable String diaryDate,
            @LoginMember CustomUserDetails userDetails) {

        Integer userId = extractUserId(userDetails);
        DiaryResponse response = diaryService.getDiaryByDate(userId, diaryDate);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "Delete diary by ID", description = "Delete diary by specific diary ID")
    @DeleteMapping("/{diaryId}")
    public ApiResponse<Void> deleteDiary(
            @Parameter(description = "Diary ID", example = "1") @PathVariable Integer diaryId,
            @LoginMember CustomUserDetails userDetails) {

        Integer userId = extractUserId(userDetails);
        diaryService.deleteDiaryById(userId, diaryId);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "다이어리 날짜 조회", description = "특정 년도 특정달에 다이어리가 쓰여진 날짜만 조회")
    @GetMapping("/calendar")
    public ApiResponse<DiaryCalendarResponse> getDiaryCalendar(
            @Parameter(description = "Year-Month (YYYY-MM format)", example = "2024-02") @RequestParam String ym,
            @LoginMember CustomUserDetails userDetails) {

        Integer userId = extractUserId(userDetails);
        DiaryCalendarResponse response = diaryService.getDiaryCalendar(userId, ym);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "Analyze diary", description = "Request AI analysis for specific diary")
    @PostMapping("/{diaryId}/analysis")
    public ApiResponse<Object> analyzeDiary(
            @Parameter(description = "Diary ID", example = "1") @PathVariable Integer diaryId,
            @LoginMember CustomUserDetails userDetails) {

        Integer userId = extractUserId(userDetails);
        Object response = diaryService.analyzeDiary(userId, diaryId);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "Get diary analysis", description = "Get AI analysis result for specific diary")
    @GetMapping("/{diaryId}/analysis")
    public ApiResponse<Object> getDiaryAnalysis(
            @Parameter(description = "Diary ID", example = "1") @PathVariable Integer diaryId,
            @LoginMember CustomUserDetails userDetails) {

        Integer userId = extractUserId(userDetails);
        Object response = diaryService.getDiaryAnalysis(userId, diaryId);
        return ApiResponse.onSuccess(response);
    }

    private Integer extractUserId(CustomUserDetails userDetails) {
        return userDetails.getUserId();
    }
}