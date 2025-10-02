package com.myocean.domain.friendchat.controller;

import com.myocean.domain.friendchat.dto.FriendResponse;
import com.myocean.domain.friendchat.entity.Friend;
import com.myocean.domain.friendchat.entity.FriendInvitation;
import com.myocean.domain.friendchat.service.FriendService;
import com.myocean.global.auth.CustomUserDetails;
import com.myocean.global.auth.LoginMember;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import com.myocean.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "Friend", description = "친구 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @Operation(summary = "친구 목록 조회", description = "쿠키 기반 인증으로 내 친구 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<List<FriendResponse>> getFriends(@LoginMember CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        List<Friend> friends = friendService.getFriends(userId);
        List<FriendResponse> friendResponses = friendService.getFriendsWithPersonaInfo(friends);
        return ApiResponse.onSuccess(friendResponses);
    }

    @Operation(summary = "친구 초대 링크 생성", description = "쿠키 기반 인증으로 친구 초대 링크를 생성합니다.")
    @PostMapping("/invites")
    public ApiResponse<Map<String, Object>> createInvitation(@LoginMember CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        Map<String, Object> invitationInfo = friendService.createInvitationWithUserInfo(userId);

        return ApiResponse.onSuccess("초대 링크가 생성되었습니다.", invitationInfo);
    }

    @Operation(summary = "친구 초대 수락", description = "쿠키 기반 인증으로 친구 초대를 수락합니다.")
    @PostMapping("/invites/{token}/accept")
    public ApiResponse<String> acceptInvitation(
            @Parameter(description = "초대 토큰") @PathVariable String token,
            @LoginMember CustomUserDetails userDetails) {

        try {
            Integer userId = userDetails.getUserId();
            friendService.acceptInvitation(token, userId);
            return ApiResponse.onSuccess("친구 초대가 수락되었습니다.");
        } catch (GeneralException e) {
            return ApiResponse.onFailure(e.getErrorStatus(), null);
        }
    }

    @Operation(summary = "친구 초대 거절", description = "쿠키 기반 인증으로 친구 초대를 거절합니다.")
    @PostMapping("/invites/{token}/decline")
    public ApiResponse<String> declineInvitation(
            @Parameter(description = "초대 토큰") @PathVariable String token,
            @LoginMember CustomUserDetails userDetails) {

        try {
            Integer userId = userDetails.getUserId();
            friendService.declineInvitation(token, userId);
            return ApiResponse.onSuccess("친구 초대가 거절되었습니다.");
        } catch (GeneralException e) {
            return ApiResponse.onFailure(e.getErrorStatus(), null);
        }
    }

    @Operation(summary = "친구 삭제", description = "쿠키 기반 인증으로 친구 관계를 해제합니다.")
    @DeleteMapping("/{friendId}")
    public ApiResponse<String> removeFriend(
            @Parameter(description = "삭제할 친구 ID") @PathVariable Integer friendId,
            @LoginMember CustomUserDetails userDetails) {

        try {
            Integer userId = userDetails.getUserId();
            friendService.removeFriend(userId, friendId);
            return ApiResponse.onSuccess("친구 관계가 해제되었습니다.");
        } catch (GeneralException e) {
            return ApiResponse.onFailure(e.getErrorStatus(), null);
        }
    }
}