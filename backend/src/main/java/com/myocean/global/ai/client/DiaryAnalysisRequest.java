package com.myocean.global.ai.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryAnalysisRequest {
    private Integer userId;
    private Integer diaryId;
    private String content;
    private String title;
}