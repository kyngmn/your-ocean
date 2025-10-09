package com.myocean.domain.user.constants;

import com.myocean.global.enums.BigCode;

import java.util.Map;

/**
 * Actor 관련 상수
 */
public class ActorConstants {

    // 기본 OCEAN Actor ID (1-5번: 공통 사용)
    public static final long OPENNESS_ACTOR_ID = 1L;         // 개방성
    public static final long CONSCIENTIOUSNESS_ACTOR_ID = 2L; // 성실성
    public static final long EXTRAVERSION_ACTOR_ID = 3L;     // 외향성
    public static final long AGREEABLENESS_ACTOR_ID = 4L;    // 친화성
    public static final long NEUROTICISM_ACTOR_ID = 5L;      // 신경성

    // BigCode → Actor ID 매핑
    private static final Map<BigCode, Long> BIG_CODE_TO_ACTOR_ID = Map.of(
            BigCode.O, OPENNESS_ACTOR_ID,
            BigCode.C, CONSCIENTIOUSNESS_ACTOR_ID,
            BigCode.E, EXTRAVERSION_ACTOR_ID,
            BigCode.A, AGREEABLENESS_ACTOR_ID,
            BigCode.N, NEUROTICISM_ACTOR_ID
    );

    /**
     * BigCode에 해당하는 기본 Actor ID 반환
     */
    public static Long getDefaultActorId(BigCode bigCode) {
        return BIG_CODE_TO_ACTOR_ID.getOrDefault(bigCode, OPENNESS_ACTOR_ID);
    }

    /**
     * 순서에 따른 Actor ID 반환 (1-5)
     */
    public static Long getActorIdByOrder(int order) {
        if (order < 1 || order > 5) {
            return OPENNESS_ACTOR_ID;
        }
        return (long) order;
    }
}
