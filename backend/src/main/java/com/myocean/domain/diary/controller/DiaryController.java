package com.myocean.domain.diary.controller;

import com.myocean.domain.diary.dto.request.DiaryCreateRequest;
import com.myocean.domain.diary.dto.response.DiaryCalendarResponse;
import com.myocean.domain.diary.dto.response.DiaryResponse;
import com.myocean.domain.diary.dto.response.DiaryAnalysisResponse;
import com.myocean.domain.diary.service.DiaryService;
import com.myocean.domain.diary.service.DiaryAnalysisStreamService;
import com.myocean.global.security.userdetails.CustomUserDetails;
import com.myocean.global.security.annotation.LoginMember;
import com.myocean.response.ApiResponse;
import com.myocean.response.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@Slf4j
@Tag(name = "Diary", description = "Diary API")
@RestController
@RequestMapping("/api/v1/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;
    private final DiaryAnalysisStreamService streamService;

    @Operation(summary = "일기 생성", description = "일기 생성")
    @PostMapping
    public ApiResponse<DiaryResponse> createDiary(
            @Valid @RequestBody DiaryCreateRequest request,
            @LoginMember CustomUserDetails userDetails
    ){
        Integer userId = extractUserId(userDetails);
        DiaryResponse response = diaryService.createDiary(userId, request);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "일기 날짜로 조회", description = "특정 날짜의 일기를 조회합니다.")
    @GetMapping
    public ApiResponse<DiaryResponse> getDiaryByDate(
            @Parameter(description = "Diary date (YYYY-MM-DD format)", example = "2024-02-15", required = true)
            @RequestParam String date,
            @LoginMember CustomUserDetails userDetails
    ) {
        Integer userId = extractUserId(userDetails);
        DiaryResponse response = diaryService.getDiaryByDate(userId, date);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "일기 ID로 조회", description = "일기 ID로 조회")
    @GetMapping("/{diaryId}")
    public ApiResponse<DiaryResponse> getDiaryById(
            @Parameter(description = "Diary ID", example = "1") @PathVariable Integer diaryId,
            @LoginMember CustomUserDetails userDetails
    ) {
        Integer userId = extractUserId(userDetails);
        DiaryResponse response = diaryService.getDiaryById(userId, diaryId);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "일기 id로 삭제", description = "일기 id로 삭제")
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

    @Operation(summary = "다이어리 분석 결과 가져오기", description = "다이어리 분석 결과 조회 (일반)")
    @GetMapping("/{diaryId}/analysis")
    public ApiResponse<DiaryAnalysisResponse> getDiaryAnalysis(
            @Parameter(description = "Diary ID", example = "1") @PathVariable Integer diaryId,
            @LoginMember CustomUserDetails userDetails
    ){
        Integer userId = extractUserId(userDetails);
        DiaryAnalysisResponse response = diaryService.getDiaryAnalysis(userId, diaryId);

        // 상태에 따라 다른 SuccessStatus 반환
        SuccessStatus status = switch (response.getStatus()) {
            case "PROCESSING" -> SuccessStatus.DIARY_ANALYSIS_PROCESSING;
            case "FAILED" -> SuccessStatus.DIARY_ANALYSIS_FAILED;
            default -> SuccessStatus.DIARY_ANALYSIS_COMPLETED;
        };

        return ApiResponse.onSuccess(status, response);
    }

    @Operation(summary = "다이어리 분석 결과 스트리밍", description = "SSE를 통해 OCEAN 메시지를 하나씩 스트리밍합니다")
    @GetMapping(value = "/{diaryId}/analysis/stream", produces = "text/event-stream")
    public SseEmitter streamDiaryAnalysis(
            @Parameter(description = "Diary ID", example = "1") @PathVariable Integer diaryId,
            @LoginMember CustomUserDetails userDetails
    ){
        Integer userId = extractUserId(userDetails);
        diaryService.getDiaryById(userId, diaryId);
        return streamService.streamAnalysisResult(diaryId);
    }

    private Integer extractUserId(CustomUserDetails userDetails) {
        return userDetails.getUserId();
    }
}