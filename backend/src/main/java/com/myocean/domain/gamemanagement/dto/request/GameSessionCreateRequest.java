package com.myocean.domain.gamemanagement.dto.request;

import com.myocean.domain.gamemanagement.enums.GameType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "게임 세션 생성 요청")
public record GameSessionCreateRequest(
        @Schema(description = "게임 타입", example = "BART", allowableValues = {"BART", "GNG", "UG"})
        @NotNull(message = "게임 타입은 필수입니다")
        GameType gameType
) {
}