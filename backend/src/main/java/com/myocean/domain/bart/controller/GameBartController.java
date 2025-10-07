package com.myocean.domain.bart.controller;

import com.myocean.domain.bart.dto.request.GameBartClickRequest;
import com.myocean.domain.bart.dto.request.GameBartFinishRoundRequest;
import com.myocean.domain.bart.dto.response.GameBartClickResponse;
import com.myocean.domain.bart.dto.response.GameBartRoundResponse;
import com.myocean.domain.bart.service.GameBartService;
import com.myocean.global.security.annotation.LoginMember;
import com.myocean.global.security.userdetails.CustomUserDetails;
import com.myocean.response.ApiResponse;
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

@RestController
@RequestMapping("/api/v1/game-sessions/{sessionId}/bart")
@RequiredArgsConstructor
@Tag(name = "BART Game", description = "BART 게임 API")
public class GameBartController {

    private final GameBartService gameBartService;

    @Operation(summary = "라운드 내 펌프 클릭 로그", description = "BART 게임에서 풍선을 펌프질하는 클릭을 기록합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "클릭 로그 기록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게임 세션을 찾을 수 없음", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content)
    })
    @PostMapping("/rounds/{roundIndex}/clicks")
    public ResponseEntity<ApiResponse<GameBartClickResponse>> recordClick(
            @LoginMember CustomUserDetails userDetails,
            @Parameter(description = "세션 ID", required = true, example = "1")
            @PathVariable Long sessionId,
            @Parameter(description = "라운드 번호 (1부터 시작)", required = true, example = "1")
            @PathVariable Integer roundIndex,
            @Valid @RequestBody GameBartClickRequest request) {

        Integer userId = userDetails.getUserId();
        GameBartClickResponse response = gameBartService.recordClick(userId, sessionId, roundIndex, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "라운드 종료", description = "BART 게임의 현재 라운드를 종료하고 결과를 기록합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "라운드 종료 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게임 세션 또는 라운드를 찾을 수 없음", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이미 종료된 라운드", content = @Content)
    })
    @PostMapping("/rounds/{roundIndex}/finish")
    public ResponseEntity<ApiResponse<GameBartRoundResponse>> finishRound(
            @LoginMember CustomUserDetails userDetails,
            @Parameter(description = "세션 ID", required = true, example = "1")
            @PathVariable Long sessionId,
            @Parameter(description = "라운드 번호 (1부터 시작)", required = true, example = "1")
            @PathVariable Integer roundIndex,
            @Valid @RequestBody GameBartFinishRoundRequest request) {

        Integer userId = userDetails.getUserId();
        GameBartRoundResponse response = gameBartService.finishRound(userId, sessionId, roundIndex, request);

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

}