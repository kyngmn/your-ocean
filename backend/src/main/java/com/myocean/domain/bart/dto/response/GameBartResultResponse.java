package com.myocean.domain.bart.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "BART 게임 결과 응답")
public record GameBartResultResponse(
        @Schema(description = "세션 ID", example = "1")
        Long sessionId,

        @Schema(description = "획득한 보상", example = "1500")
        Integer rewardAmount,

        @Schema(description = "놓친 보상", example = "300")
        Integer missedReward,

        @Schema(description = "총 풍선 개수", example = "30")
        Integer totalBalloons,

        @Schema(description = "성공한 풍선", example = "25")
        Integer successBalloons,

        @Schema(description = "실패한 풍선", example = "5")
        Integer failBalloons,

        @Schema(description = "평균 펌핑 횟수", example = "8.5")
        BigDecimal avgPumps
) {
}
