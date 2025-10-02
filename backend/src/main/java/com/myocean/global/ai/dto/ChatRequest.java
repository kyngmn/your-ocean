package com.myocean.global.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    private Integer userId;
    private String message;
    private String chatType; // "my", "diary", "friend"
    private Integer diaryId;
    private Map<String, Double> big5Scores;
}