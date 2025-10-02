package com.myocean.domain.friendchat.controller;

import com.myocean.domain.friendchat.dto.FriendChatRequest;
import com.myocean.domain.friendchat.dto.FriendChatResponse;
import com.myocean.domain.friendchat.service.FriendChatService;
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
import com.myocean.response.ApiResponse;

@RestController
@RequestMapping("/api/friend-chat")
@RequiredArgsConstructor
@Tag(name = "FriendChat", description = "친구 AI 중재 채팅 API")
public class FriendChatController {

    private final FriendChatService friendChatService;

    @Operation(summary = "친구 AI 중재 채팅", description = "친구와의 채팅에서 AI가 중재 역할을 수행합니다.")
    @PostMapping("/send")
    public ApiResponse<FriendChatResponse> sendMessage(
            @Parameter(description = "사용자 ID", required = true, example = "1")
            @RequestHeader("User-Id") Integer userId,
            @Valid @RequestBody FriendChatRequest request) {

        FriendChatResponse response = friendChatService.sendMessage(userId, request);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "친구 채팅 기록 조회", description = "특정 친구 방의 AI 중재 채팅 기록을 조회합니다.")
    @GetMapping("/history/{roomId}")
    public ApiResponse<Page<FriendChatResponse>> getChatHistory(
            @Parameter(description = "사용자 ID", required = true, example = "1")
            @RequestHeader("User-Id") Integer userId,
            @Parameter(description = "친구 방 ID", required = true, example = "1")
            @PathVariable Integer roomId,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<FriendChatResponse> chatHistory = friendChatService.getChatHistory(userId, roomId, pageable);
        return ApiResponse.onSuccess(chatHistory);
    }

    @Operation(summary = "친구 채팅 수 조회", description = "특정 친구 방의 총 채팅 메시지 수를 조회합니다.")
    @GetMapping("/count/{roomId}")
    public ApiResponse<Long> getChatCount(
            @Parameter(description = "사용자 ID", required = true, example = "1")
            @RequestHeader("User-Id") Integer userId,
            @Parameter(description = "친구 방 ID", required = true, example = "1")
            @PathVariable Integer roomId) {

        Long count = friendChatService.getChatCount(userId, roomId);
        return ApiResponse.onSuccess(count);
    }

    @Operation(summary = "사용자 전체 친구 채팅 기록", description = "사용자의 모든 친구 채팅 기록을 조회합니다.")
    @GetMapping("/user-history")
    public ApiResponse<Page<FriendChatResponse>> getUserChatHistory(
            @Parameter(description = "사용자 ID", required = true, example = "1")
            @RequestHeader("User-Id") Integer userId,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<FriendChatResponse> chatHistory = friendChatService.getUserChatHistory(userId, pageable);
        return ApiResponse.onSuccess(chatHistory);
    }
}