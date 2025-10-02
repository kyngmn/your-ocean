package com.myocean.domain.gamemanagement.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record GameGngResultResponse(
        Long sessionId,
        Integer totalCorrectCnt,
        Integer totalIncorrectCnt,
        Integer nogoIncorrectCnt,
        BigDecimal avgReactionTime,
        LocalDateTime playedAt,
        LocalDateTime finishedAt
) {
}
