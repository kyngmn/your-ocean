package com.myocean.domain.friendchat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "친구 채팅 요청")
public class FriendChatRequest {

    @NotNull(message = "친구 관계 ID는 필수입니다")
    @Schema(description = "친구 관계 ID (friends 테이블의 room_id)", example = "1")
    private Integer roomId;

    @NotBlank(message = "메시지는 필수입니다")
    @Size(max = 2000, message = "메시지는 2000자 이하여야 합니다")
    @Schema(description = "채팅 메시지", example = "친구야, 오늘 어떻게 지냈어?")
    private String message;
}