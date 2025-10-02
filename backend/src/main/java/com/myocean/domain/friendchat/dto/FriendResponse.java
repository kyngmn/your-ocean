package com.myocean.domain.friendchat.dto;

import com.myocean.domain.friendchat.entity.Friend;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "친구 응답")
public record FriendResponse(
        @Schema(description = "친구 관계 ID", example = "1")
        Integer id,

        @Schema(description = "사용자 ID", example = "1")
        Integer userId,

        @Schema(description = "친구 ID", example = "2")
        Integer friendId,

        @Schema(description = "친구 닉네임", example = "친구닉네임")
        String friendNickname,

        @Schema(description = "친구 프로필 이미지", example = "https://example.com/profile.jpg")
        String friendProfileImageUrl,

        @Schema(description = "페르소나 생성 여부", example = "true")
        Boolean hasPersona,

        @Schema(description = "친구 추가 일시")
        LocalDateTime createdAt
) {
    public static FriendResponse from(Friend friend) {
        return new FriendResponse(
                friend.getId(),
                friend.getUserId(),
                friend.getFriendId(),
                friend.getFriend() != null ? friend.getFriend().getNickname() : null,
                friend.getFriend() != null ? friend.getFriend().getProfileImageUrl() : null,
                false, // hasPersona는 서비스에서 별도로 설정
                friend.getCreatedAt()
        );
    }

    public static FriendResponse from(Friend friend, Boolean hasPersona) {
        return new FriendResponse(
                friend.getId(),
                friend.getUserId(),
                friend.getFriendId(),
                friend.getFriend() != null ? friend.getFriend().getNickname() : null,
                friend.getFriend() != null ? friend.getFriend().getProfileImageUrl() : null,
                hasPersona,
                friend.getCreatedAt()
        );
    }
}