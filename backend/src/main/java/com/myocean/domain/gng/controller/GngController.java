package com.myocean.domain.gng.controller;

import com.myocean.domain.gng.dto.request.GngResponseCreateRequest;
import com.myocean.domain.gng.service.GngService;
import com.myocean.global.auth.CustomUserDetails;
import com.myocean.global.auth.LoginMember;
import com.myocean.response.ApiResponse;
import com.myocean.response.status.SuccessStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/game-sessions")
@RequiredArgsConstructor
@Tag(name = "GNG Game", description = "Go/No-Go 게임 API")
public class GngController {

    private final GngService gngService;

    @PostMapping("/{sessionId}/gng/rounds/{roundIndex}/clicks")
    public ApiResponse<String> saveGngResponse(
            @LoginMember CustomUserDetails userDetails,
            @PathVariable Long sessionId,
            @PathVariable Integer roundIndex,
            @Valid @RequestBody GngResponseCreateRequest request) {

        Integer userId = userDetails.getUser().getId();
        gngService.saveGngResponse(userId, sessionId, roundIndex, request);
        return ApiResponse.onSuccess(SuccessStatus.GAME_RESPONSE_SAVED, SuccessStatus.GAME_RESPONSE_SAVED.getMessage());
    }
}