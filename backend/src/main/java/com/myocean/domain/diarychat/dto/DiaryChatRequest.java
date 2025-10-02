package com.myocean.domain.diarychat.dto;

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
@Schema(description = "다이어리 채팅 요청")
public class DiaryChatRequest {

    @NotNull(message = "다이어리 ID는 필수입니다")
    @Schema(description = "다이어리 ID", example = "1")
    private Integer diaryId;

    @NotBlank(message = "메시지는 필수입니다")
    @Size(max = 2000, message = "메시지는 2000자 이하여야 합니다")
    @Schema(description = "채팅 메시지", example = "이 일기에 대해 더 자세히 이야기하고 싶어요")
    private String message;
}