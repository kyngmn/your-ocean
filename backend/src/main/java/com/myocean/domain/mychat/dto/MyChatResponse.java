package com.myocean.domain.mychat.dto;

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
@Schema(description = "개인 채팅 응답")
public class MyChatResponse {

    @Schema(description = "메시지 ID", example = "1")
    private Long id;

    @Schema(description = "메시지 내용", example = "안녕하세요! 오늘 기분이 어떠신가요?")
    private String message;

    @Schema(description = "발신자 종류", example = "PERSONA")
    private ActorKind senderKind;

    @Schema(description = "발신자 액터 ID", example = "1")
    private Integer senderActorId;

    @Schema(description = "읽음 여부", example = "false")
    private Boolean isRead;

    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;
}