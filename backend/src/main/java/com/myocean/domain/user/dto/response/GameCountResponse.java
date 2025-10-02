package com.myocean.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 게임 카운트 응답")
public record GameCountResponse(
        @Schema(description = "GNG 게임 플레이 횟수", example = "2")
        Integer gng,

        @Schema(description = "UG 게임 플레이 횟수", example = "4")
        Integer ug,

        @Schema(description = "BART 게임 플레이 횟수", example = "1")
        Integer bart
) {
}