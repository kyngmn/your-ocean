package com.myocean.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "유저 페르소나 응답")
public record UserPersonaResponse(
        @Schema(description = "페르소나 ID", example = "1")
        Integer id,

        @Schema(description = "유저 ID", example = "1")
        Integer userId,

        @Schema(description = "개방성 점수", example = "75")
        Integer userO,

        @Schema(description = "성실성 점수", example = "80")
        Integer userC,

        @Schema(description = "외향성 점수", example = "65")
        Integer userE,

        @Schema(description = "친화성 점수", example = "70")
        Integer userA,

        @Schema(description = "신경성 점수", example = "60")
        Integer userN,

        @Schema(description = "생성일시")
        LocalDateTime createdAt,

        @Schema(description = "수정일시")
        LocalDateTime updatedAt
) {
}