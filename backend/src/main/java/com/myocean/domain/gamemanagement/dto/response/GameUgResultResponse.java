package com.myocean.domain.gamemanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameUgResultResponse {
    private Long sessionId;
    private Integer earnedAmount;
    private LocalDateTime finishedAt;
}
