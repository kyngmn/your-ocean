package com.myocean.domain.friendchat.dto;

import com.myocean.domain.friendchat.entity.FriendChatMessage;
import com.myocean.domain.user.enums.ActorKind;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "친구 채팅 응답")
public class FriendChatResponse {

    @Schema(description = "메시지 ID", example = "1")
    private Integer id;

    @Schema(description = "친구 방 ID", example = "1")
    private Integer roomId;

    @Schema(description = "메시지 내용", example = "두 분의 대화를 듣고 있어요. 서로의 마음을 더 깊이 이해해보세요.")
    private String message;

    @Schema(description = "발신자 종류", example = "PERSONA")
    private ActorKind senderKind;

    @Schema(description = "발신자 액터 ID", example = "1")
    private Integer senderActorId;

    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    public static FriendChatResponse from(FriendChatMessage chatMessage) {
        return FriendChatResponse.builder()
                .id(chatMessage.getId())
                .roomId(chatMessage.getRoomId())
                .message(chatMessage.getMessage())
                .senderKind(chatMessage.getSenderActor() != null ? 
                           chatMessage.getSenderActor().getKind() : null)
                .senderActorId(chatMessage.getSenderActorId())
                .createdAt(chatMessage.getCreatedAt())
                .build();
    }
}