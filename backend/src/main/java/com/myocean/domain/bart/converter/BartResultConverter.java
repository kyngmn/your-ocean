package com.myocean.domain.bart.converter;

import com.myocean.domain.bart.dto.response.GameBartResultResponse;
import com.myocean.domain.bart.entity.GameBartResult;

public class BartResultConverter {

    public static GameBartResultResponse toResponse(GameBartResult result) {
        return new GameBartResultResponse(
                result.getSessionId(),
                result.getRewardAmount(),
                result.getMissedReward(),
                result.getTotalBalloons(),
                result.getSuccessBalloons(),
                result.getFailBalloons(),
                result.getAvgPumps()
        );
    }
}
