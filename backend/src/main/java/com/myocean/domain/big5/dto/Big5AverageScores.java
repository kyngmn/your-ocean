package com.myocean.domain.big5.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * Big5 게임 결과의 평균 점수를 담는 DTO
 */
@Getter
@AllArgsConstructor
public class Big5AverageScores {

    private final Integer o;  // Openness (개방성)
    private final Integer c;  // Conscientiousness (성실성)
    private final Integer e;  // Extraversion (외향성)
    private final Integer a;  // Agreeableness (친화성)
    private final Integer n;  // Neuroticism (신경성)

    /**
     * Big5 점수를 Map으로 변환 (null인 경우 기본값 50 사용)
     */
    public Map<String, Integer> toMap() {
        return Map.of(
                "O", o != null ? o : 50,
                "C", c != null ? c : 50,
                "E", e != null ? e : 50,
                "A", a != null ? a : 50,
                "N", n != null ? n : 50
        );
    }
}
