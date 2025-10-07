package com.myocean.domain.diarychat.controller;

import com.myocean.domain.diarychat.dto.DiaryChatRequest;
import com.myocean.domain.diarychat.dto.DiaryChatResponse;
import com.myocean.domain.diarychat.service.DiaryChatService;
import com.myocean.global.security.userdetails.CustomUserDetails;
import com.myocean.global.security.annotation.LoginMember;
import com.myocean.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diary-chat")
@RequiredArgsConstructor
@Tag(name = "DiaryChat", description = "다이어리 기반 AI 채팅 API")
public class DiaryChatController {

    private final DiaryChatService diaryChatService;

    @Operation(summary = "다이어리 AI 채팅", description = "특정 다이어리에 대해 AI와 채팅을 진행합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "채팅 메시지 전송 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "다이어리를 찾을 수 없음", content = @Content)
    })
    @PostMapping("/send")
    public ApiResponse<DiaryChatResponse> sendMessage(
            @LoginMember CustomUserDetails userDetails,
            @Valid @RequestBody DiaryChatRequest request) {

        Integer userId = userDetails.getUserId();
        DiaryChatResponse response = diaryChatService.sendMessage(userId, request);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "다이어리 채팅 기록 조회", description = "특정 다이어리의 AI 채팅 기록을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/history/{diaryId}")
    public ApiResponse<Page<DiaryChatResponse>> getChatHistory(
            @LoginMember CustomUserDetails userDetails,
            @Parameter(description = "다이어리 ID", required = true, example = "1")
            @PathVariable Integer diaryId,
            @PageableDefault(size = 20) Pageable pageable) {

        Integer userId = userDetails.getUserId();
        Page<DiaryChatResponse> chatHistory = diaryChatService.getChatHistory(userId, diaryId, pageable);
        return ApiResponse.onSuccess(chatHistory);
    }

    @Operation(summary = "다이어리 채팅 수 조회", description = "특정 다이어리의 총 채팅 메시지 수를 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/count/{diaryId}")
    public ApiResponse<Long> getChatCount(
            @LoginMember CustomUserDetails userDetails,
            @Parameter(description = "다이어리 ID", required = true, example = "1")
            @PathVariable Integer diaryId) {

        Integer userId = userDetails.getUserId();
        Long count = diaryChatService.getChatCount(userId, diaryId);
        return ApiResponse.onSuccess(count);
    }

    @Operation(summary = "사용자 전체 다이어리 채팅 기록", description = "사용자의 모든 다이어리 채팅 기록을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/user-history")
    public ApiResponse<Page<DiaryChatResponse>> getUserChatHistory(
            @LoginMember CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {

        Integer userId = userDetails.getUserId();
        Page<DiaryChatResponse> chatHistory = diaryChatService.getUserChatHistory(userId, pageable);
        return ApiResponse.onSuccess(chatHistory);
    }
}
