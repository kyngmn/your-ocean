package com.myocean.global.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private boolean success;
    private String message;
    private String agentType;
    // private Big5ScoresDto big5Scores; // TODO: Big5ScoresDto 클래스 필요
}