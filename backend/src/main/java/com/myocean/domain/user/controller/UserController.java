package com.myocean.domain.user.controller;

import com.myocean.domain.user.dto.response.UserPersonaResponse;
import com.myocean.domain.user.dto.response.UserResponse;
import com.myocean.domain.user.dto.response.GameCountResponse;
import com.myocean.domain.user.service.UserPersonaService;
import com.myocean.domain.user.service.UserService;
import com.myocean.domain.user.service.GameCountService;
import com.myocean.global.openai.dailymessage.dto.DailyMessageResponse;
import com.myocean.global.openai.dailymessage.service.DailyMessageService;
import com.myocean.global.auth.CustomUserDetails;
import com.myocean.global.auth.LoginMember;
import com.myocean.response.ApiResponse;
import com.myocean.response.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "유저 관리 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserPersonaService userPersonaService;
    private final GameCountService gameCountService;
    private final DailyMessageService dailyMessageService;

    @Operation(summary = "내 프로필 조회", description = "쿠키 기반 인증으로 현재 로그인한 사용자 정보를 조회합니다.")
    @GetMapping
    public ApiResponse<UserResponse> getCurrentUser(
            @LoginMember CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        UserResponse user = userService.getCurrentUser(userId);
        return ApiResponse.onSuccess(user);
    }

    @Operation(summary = "닉네임/이미지 등 수정", description = "쿠키 기반 인증으로 현재 사용자의 프로필 정보를 수정합니다.")
    @PatchMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ApiResponse<UserResponse> updateUser(
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) MultipartFile file,
            @LoginMember CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        UserResponse user = userService.updateUserProfile(userId, nickname, file);
        return ApiResponse.onSuccess(user);
    }

    @Operation(summary = "계정 삭제", description = "쿠키 기반 인증으로 현재 사용자의 계정을 삭제합니다.")
    @DeleteMapping
    public ApiResponse<String> deleteUser(
            @LoginMember CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        userService.deleteUser(userId);
        return ApiResponse.onSuccess("회원 탈퇴가 완료되었습니다.");
    }

    @Operation(summary = "유저 페르소나 조회", description = "특정 유저의 페르소나를 조회하고 존재 여부를 확인합니다.")
    @GetMapping("/personas")
    public ApiResponse<UserPersonaResponse> getUserPersona(
            @LoginMember CustomUserDetails userDetails) {

        Integer userId = userDetails.getUserId();
        UserPersonaResponse response = userPersonaService.getUserPersona(userId);

        if (response == null) {
            return ApiResponse.onSuccess(SuccessStatus.PERSONA_NOT_EXISTS, null);
        }
        return ApiResponse.onSuccess(SuccessStatus.PERSONA_EXISTS, response);
    }

    @Operation(summary = "닉네임 중복 확인", description = "닉네임 사용 가능 여부를 확인합니다. 형식 검사와 중복 검사를 함께 수행합니다.")
    @GetMapping("/check-nickname")
    public ApiResponse<Boolean> checkNicknameAvailability(
            @Parameter(description = "확인할 닉네임 (2-10글자, 한글/영문/숫자만 허용)")
            @RequestParam String nickname) {
        boolean isAvailable = userService.isNicknameAvailable(nickname);

        if (isAvailable) {
            return ApiResponse.onSuccess("사용 가능한 닉네임입니다.", true);
        } else {
            return ApiResponse.onSuccess("이미 사용 중인 닉네임입니다.", false);
        }
    }

    @Operation(summary = "내 게임 카운트 조회", description = "현재 유저의 게임별 플레이 횟수를 조회합니다.")
    @GetMapping("/games/count")
    public ApiResponse<GameCountResponse> getUserGameCounts(
            @LoginMember CustomUserDetails userDetails
    ) {
        Integer userId = userDetails.getUserId();
        GameCountResponse gameCountResponse = gameCountService.getGameCountResponse(userId);
        return ApiResponse.onSuccess(gameCountResponse);
    }

    @Operation(summary = "오늘의 말", description = "Big5 성격 특성 중 하나를 랜덤으로 선택하여 해당 특성에 맞는 오늘의 말을 제공합니다.")
    @GetMapping("/daily-message")
    public ApiResponse<DailyMessageResponse> getDailyMessage() {
        DailyMessageResponse response = dailyMessageService.getDailyMessage();
        return ApiResponse.onSuccess(response);
    }
}
