package com.myocean.domain.ug.controller;

import com.myocean.domain.ug.dto.request.GameUgResponseRequest;
import com.myocean.domain.ug.dto.response.GameUgOrderResponse;
import com.myocean.domain.ug.dto.response.GameUgResponseDto;
import com.myocean.domain.ug.service.GameUgService;
import com.myocean.global.auth.CustomUserDetails;
import com.myocean.global.auth.LoginMember;
import com.myocean.response.ApiResponse;
import com.myocean.response.status.ErrorStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "UG Game", description = "Ultimatum Game API")
public class GameUgController {

    private final GameUgService gameUgService;

    @Operation(summary = "라운드 오더 조회", description = "UG 게임의 역할, 조건, 제안/응답 순서 등을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "오더 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 에러", content = @Content)
    })
    @GetMapping("/api/v1/games/ug/order")
    public ResponseEntity<ApiResponse<List<GameUgOrderResponse>>> getGameOrders(
            @Parameter(description = "게임 일차 (1, 2, 3)", example = "1")
            @RequestParam(required = false) Integer day) {

        List<GameUgOrderResponse> response;
        if (day != null) {
            // Day별 조회
            if (day < 1 || day > 3) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.onFailure(ErrorStatus.UG_ORDER_NOT_FOUND, null));
            }
            response = gameUgService.getGameOrdersByDay(day);
        } else {
            // 전체 조회
            response = gameUgService.getGameOrders();
        }

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "라운드 응답 로그 제출", description = "UG 게임에서 제안 금액과 수락 여부를 제출합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "응답 제출 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게임 세션을 찾을 수 없음", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content)
    })
    @PostMapping("/api/v1/game-sessions/{sessionId}/ug/rounds/{roundId}/responses")
    public ResponseEntity<ApiResponse<GameUgResponseDto>> submitGameResponse(
            @LoginMember CustomUserDetails userDetails,
            @Parameter(description = "세션 ID", required = true, example = "1")
            @PathVariable Long sessionId,
            @Parameter(description = "라운드 ID", required = true, example = "1")
            @PathVariable Integer roundId,
            @Valid @RequestBody GameUgResponseRequest request) {

        Integer userId = userDetails.getUserId();
        GameUgResponseDto response = gameUgService.submitGameResponse(userId, sessionId, roundId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(response));
    }


    @Operation(summary = "세션별 오더 조회", description = "게임 세션에 맞는 UG 오더를 자동으로 조회합니다. 사용자의 완료 횟수에 따라 Day가 결정됩니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "오더 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "세션을 찾을 수 없음", content = @Content)
    })
    @GetMapping("/api/v1/game-sessions/{sessionId}/ug/orders")
    public ResponseEntity<ApiResponse<List<GameUgOrderResponse>>> getGameOrdersBySession(
            @LoginMember CustomUserDetails userDetails,
            @Parameter(description = "세션 ID", required = true, example = "1")
            @PathVariable Long sessionId) {

        List<GameUgOrderResponse> response = gameUgService.getGameOrdersBySession(sessionId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }


}