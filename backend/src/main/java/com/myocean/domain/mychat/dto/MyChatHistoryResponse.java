package com.myocean.domain.mychat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "채팅 히스토리 응답")
public class MyChatHistoryResponse {

    @Schema(description = "채팅 메시지 목록")
    private List<MyChatResponse> messages;

    @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
    private int page;

    @Schema(description = "페이지 크기", example = "20")
    private int size;

    @Schema(description = "전체 메시지 수", example = "150")
    private long totalElements;

    @Schema(description = "전체 페이지 수", example = "8")
    private int totalPages;

    @Schema(description = "첫 번째 페이지 여부", example = "true")
    private boolean first;

    @Schema(description = "마지막 페이지 여부", example = "false")
    private boolean last;

    public static MyChatHistoryResponse from(Page<MyChatResponse> page) {
        return MyChatHistoryResponse.builder()
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