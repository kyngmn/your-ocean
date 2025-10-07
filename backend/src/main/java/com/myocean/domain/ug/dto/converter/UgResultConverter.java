package com.myocean.domain.ug.dto.converter;

import com.myocean.domain.ug.dto.response.GameUgResultResponse;
import com.myocean.domain.ug.entity.GameUgResult;

public class UgResultConverter {

    public static GameUgResultResponse toResponse(GameUgResult ugResult) {
        return GameUgResultResponse.builder()
                .sessionId(ugResult.getSessionId())
                .earnedAmount(ugResult.getEarnedAmount())
                .finishedAt(ugResult.getFinishedAt())
                .build();
    }
}