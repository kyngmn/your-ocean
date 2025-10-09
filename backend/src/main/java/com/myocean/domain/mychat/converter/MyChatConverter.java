package com.myocean.domain.mychat.converter;

import com.myocean.domain.mychat.dto.response.MyChatPageResponse;
import com.myocean.domain.mychat.dto.response.MyChatResponse;
import com.myocean.domain.mychat.entity.MyChatMessage;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;

@UtilityClass
public class MyChatConverter {

    public static MyChatResponse toResponse(MyChatMessage chatMessage) {
        return MyChatResponse.builder()
                .id(chatMessage.getId())
                .message(chatMessage.getMessage())
                .senderKind(chatMessage.getSenderActor().getKind())
                .senderActorId(chatMessage.getSenderActor().getId())
                .isRead(chatMessage.getIsRead())
                .createdAt(chatMessage.getCreatedAt())
                .build();
    }

    public static MyChatPageResponse toHistoryResponse(Page<MyChatResponse> page) {
        return MyChatPageResponse.builder()
                .messages(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}