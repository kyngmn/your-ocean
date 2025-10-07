package com.myocean.global.openai.personality.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalityComparisonRequest {
    private Map<String, Integer> selfScores;    // 자기보고식 Big5 점수
    private Map<String, Integer> gameScores;    // 게임 행동 기반 Big5 점수

    public static PersonalityComparisonRequest of(
            Map<String, Integer> selfScores,
            Map<String, Integer> gameScores) {
        return new PersonalityComparisonRequest(selfScores, gameScores);
    }
}