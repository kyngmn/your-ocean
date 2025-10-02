package com.myocean.domain.diarychat.dto;

import com.myocean.domain.diarychat.entity.DiaryChatMessage;
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
@Schema(description = "다이어리 채팅 응답")
public class DiaryChatResponse {

    @Schema(description = "메시지 ID", example = "1")
    private Long id;

    @Schema(description = "다이어리 ID", example = "1")
    private Integer diaryId;

    @Schema(description = "메시지 내용", example = "일기를 읽어보니 힘든 하루였을 것 같네요. 어떤 부분이 가장 어려우셨나요?")
    private String message;

    @Schema(description = "발신자 종류", example = "PERSONA")
    private ActorKind senderKind;

    @Schema(description = "발신자 액터 ID", example = "1")
    private Integer senderActorId;

    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    public static DiaryChatResponse from(DiaryChatMessage chatMessage) {
        return DiaryChatResponse.builder()
                .id(chatMessage.getId())
                .diaryId(chatMessage.getDiaryId())
                .message(chatMessage.getMessage())
                .senderKind(chatMessage.getSenderActor() != null ? 
                           chatMessage.getSenderActor().getKind() : null)
                .senderActorId(chatMessage.getSenderActorId())
                .createdAt(chatMessage.getCreatedAt())
                .build();
    }
}