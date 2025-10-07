package com.myocean.domain.gng.dto.converter;

import com.myocean.domain.gng.dto.response.GameGngResultResponse;
import com.myocean.domain.gng.entity.GameGngResult;

public class GngResultConverter {

    public static GameGngResultResponse toResponse(GameGngResult gngResult) {
        return GameGngResultResponse.builder()
                .sessionId(gngResult.getSessionId())
                .totalCorrectCnt(gngResult.getTotalCorrectCnt())
                .totalIncorrectCnt(gngResult.getTotalIncorrectCnt())
                .nogoIncorrectCnt(gngResult.getNogoIncorrectCnt())
                .avgReactionTime(gngResult.getAvgReactionTime())
                .playedAt(gngResult.getPlayedAt())
                .finishedAt(gngResult.getFinishedAt())
                .build();
    }
}
