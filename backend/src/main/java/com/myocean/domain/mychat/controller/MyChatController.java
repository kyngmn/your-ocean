package com.myocean.domain.mychat.controller;

import com.myocean.domain.mychat.dto.MyChatRequest;
import com.myocean.domain.mychat.dto.MyChatResponse;
import com.myocean.domain.mychat.dto.MyChatHistoryResponse;
import com.myocean.domain.mychat.service.MyChatService;
import com.myocean.global.security.userdetails.CustomUserDetails;
import com.myocean.global.security.annotation.LoginMember;
import com.myocean.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/my-chat")
@RequiredArgsConstructor
@Tag(name = "MyChat", description = "개인 AI 채팅 API")
public class MyChatController {

    private final MyChatService myChatService;

    @Operation(summary = "AI와 채팅", description = "AI 페르소나와 개인 채팅을 진행합니다.")
    @PostMapping("/send")
    public ApiResponse<MyChatResponse> sendMessage(
            @Valid @RequestBody MyChatRequest request,
            @LoginMember CustomUserDetails userDetails) {

        Integer userId = extractUserId(userDetails);
        MyChatResponse response = myChatService.sendMessage(userId, request);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "채팅 기록 조회", description = "사용자의 AI 채팅 기록을 페이지네이션으로 조회합니다.")
    @GetMapping("/history")
    public ApiResponse<MyChatHistoryResponse> getChatHistory(
            @PageableDefault(size = 20) Pageable pageable,
            @LoginMember CustomUserDetails userDetails
    ) {
        Integer userId = extractUserId(userDetails);
        Page<MyChatResponse> chatHistory = myChatService.getChatHistory(userId, pageable);
        MyChatHistoryResponse response = MyChatHistoryResponse.from(chatHistory);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "채팅 수 조회", description = "사용자의 총 채팅 메시지 수를 조회합니다.")
    @GetMapping("/count")
    public ApiResponse<Long> getChatCount(
            @LoginMember CustomUserDetails userDetails) {

        Integer userId = extractUserId(userDetails);
        Long count = myChatService.getChatCount(userId);
        return ApiResponse.onSuccess(count);
    }

    @Operation(summary = "안읽은 메시지 조회", description = "사용자의 안읽은 AI 응답 메시지들을 조회합니다. (폴링용)")
    @GetMapping("/unread")
    public ApiResponse<List<MyChatResponse>> getUnreadMessages(
            @LoginMember CustomUserDetails userDetails) {

        Integer userId = extractUserId(userDetails);
        List<MyChatResponse> unreadMessages = myChatService.getUnreadMessages(userId);
        return ApiResponse.onSuccess(unreadMessages);
    }

    @Operation(summary = "안읽은 메시지 개수", description = "사용자의 안읽은 메시지 개수를 조회합니다.")
    @GetMapping("/unread/count")
    public ApiResponse<Long> getUnreadCount(
            @LoginMember CustomUserDetails userDetails) {

        Integer userId = extractUserId(userDetails);
        Long unreadCount = myChatService.getUnreadCount(userId);
        return ApiResponse.onSuccess(unreadCount);
    }

    private Integer extractUserId(CustomUserDetails userDetails) {
        return userDetails.getUserId();
    }
}