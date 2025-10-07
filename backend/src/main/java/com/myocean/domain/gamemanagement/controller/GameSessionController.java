package com.myocean.domain.gamemanagement.controller;

import com.myocean.domain.gamemanagement.dto.request.GameSessionCreateRequest;
import com.myocean.domain.gamemanagement.dto.response.GameSessionResponse;
import com.myocean.domain.gamemanagement.service.GameSessionService;
import com.myocean.global.security.userdetails.CustomUserDetails;
import com.myocean.global.security.annotation.LoginMember;
import com.myocean.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1/game-sessions")
@RequiredArgsConstructor
@Tag(name = "Game Session", description = "게임 세션 관리 API")
public class GameSessionController {

    private final GameSessionService gameSessionService;

    @Operation(summary = "게임 세션 생성", description = "새로운 게임 세션을 생성합니다.")
    @PostMapping
    public ApiResponse<GameSessionResponse> createGameSession(
            @LoginMember CustomUserDetails customUserDetails,
            @Valid @RequestBody GameSessionCreateRequest request) {

        Integer userId = customUserDetails.getUserId();
        GameSessionResponse response = gameSessionService.createGameSession(userId, request);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "세션 결과 조회 (게임 결과 조회)", description = "완료된 게임 세션의 결과를 조회합니다.")
    @GetMapping("/{sessionId}/result")
    public ResponseEntity<ApiResponse<Object>> getGameSessionResult(
            @LoginMember CustomUserDetails userDetails,
            @Parameter(description = "세션 ID", required = true, example = "1")
            @PathVariable Long sessionId) {

        Integer userId = userDetails.getUserId();
        Object response = gameSessionService.getGameSessionResult(userId, sessionId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "세션 종료", description = "진행 중인 게임 세션을 종료합니다.")
    @PatchMapping("/{sessionId}/finish")
    public ApiResponse<GameSessionResponse> finishGameSession(
            @LoginMember CustomUserDetails userDetails,
            @Parameter(description = "세션 ID", required = true, example = "1")
            @PathVariable Long sessionId) {

        Integer userId = userDetails.getUser().getId();
        GameSessionResponse response = gameSessionService.finishGameSession(userId, sessionId);
        return ApiResponse.onSuccess(response);
    }
}