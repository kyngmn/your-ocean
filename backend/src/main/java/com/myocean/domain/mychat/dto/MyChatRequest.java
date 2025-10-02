package com.myocean.domain.mychat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "개인 채팅 요청")
public class MyChatRequest {

    @NotBlank(message = "메시지는 필수입니다")
    @Size(max = 2000, message = "메시지는 2000자 이하여야 합니다")
    @Schema(description = "채팅 메시지", example = "오늘 기분이 좋지 않아요")
    private String message;
}