package com.myocean.domain.mychat.converter;

import com.myocean.domain.mychat.dto.MyChatResponse;
import com.myocean.domain.mychat.entity.MyChatMessage;

public class MyChatConverter {

    public static MyChatResponse toResponse(MyChatMessage chatMessage) {
        return MyChatResponse.builder()
                .id(chatMessage.getId())
                .message(chatMessage.getMessage())
                .senderKind(chatMessage.getSenderActor() != null ?
                           chatMessage.getSenderActor().getKind() : null)
                .senderActorId(chatMessage.getSenderActorId())
                .isRead(chatMessage.getIsRead())
                .createdAt(chatMessage.getCreatedAt())
                .build();
    }
}