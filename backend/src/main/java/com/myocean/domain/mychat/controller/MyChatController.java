package com.myocean.domain.mychat.controller;

import com.myocean.domain.mychat.dto.MyChatRequest;
import com.myocean.domain.mychat.dto.MyChatResponse;
import com.myocean.domain.mychat.service.MyChatService;
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
@RequestMapping("/api/v1/my-chat")
@RequiredArgsConstructor
@Tag(name = "MyChat", description = "개인 AI 채팅 API")
public class MyChatController {

    private final MyChatService myChatService;

    @Operation(summary = "AI와 채팅", description = "AI 페르소나와 개인 채팅을 진행합니다.")
    @PostMapping("/send")
    public ApiResponse<MyChatResponse> sendMessage(
            @Parameter(description = "사용자 ID", required = true, example = "1")
            @RequestHeader("User-Id") Integer userId,
            @Valid @RequestBody MyChatRequest request) {

        MyChatResponse response = myChatService.sendMessage(userId, request);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "채팅 기록 조회", description = "사용자의 AI 채팅 기록을 페이지네이션으로 조회합니다.")
    @GetMapping("/history")
    public ApiResponse<Page<MyChatResponse>> getChatHistory(
            @Parameter(description = "사용자 ID", required = true, example = "1")
            @RequestHeader("User-Id") Integer userId,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<MyChatResponse> chatHistory = myChatService.getChatHistory(userId, pageable);
        return ApiResponse.onSuccess(chatHistory);
    }

    @Operation(summary = "채팅 수 조회", description = "사용자의 총 채팅 메시지 수를 조회합니다.")
    @GetMapping("/count")
    public ApiResponse<Long> getChatCount(
            @Parameter(description = "사용자 ID", required = true, example = "1")
            @RequestHeader("User-Id") Integer userId) {

        Long count = myChatService.getChatCount(userId);
        return ApiResponse.onSuccess(count);
    }
}