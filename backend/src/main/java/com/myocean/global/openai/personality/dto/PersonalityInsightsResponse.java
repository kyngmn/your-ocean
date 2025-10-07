package com.myocean.global.openai.personality.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalityInsightsResponse {
    private String headline;        // "전체를 관통하는 1문장(메타포/이미지 중심)"
    private Insights insights;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Insights {
        private String main;        // "당신의 하루 전반을 그리는 짧은 단락"
        private String gap;         // "서로 다른 두 얼굴이 어떻게 조화되는지"
        private String strength;    // "당신만의 강점이 빛나는 순간과 에너지"
    }
}