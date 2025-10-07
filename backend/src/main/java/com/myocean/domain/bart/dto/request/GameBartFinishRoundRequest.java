package com.myocean.domain.bart.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "BART 게임 라운드 종료 요청")
public record GameBartFinishRoundRequest(
        @Schema(description = "풍선이 터졌는지 여부", example = "false", required = true)
        @NotNull(message = "풍선 터짐 여부는 필수입니다")
        Boolean isPopped
) {
}