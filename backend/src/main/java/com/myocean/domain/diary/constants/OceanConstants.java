package com.myocean.domain.diary.constants;

import lombok.experimental.UtilityClass;

import java.util.Map;

/**
 * OCEAN 모델 관련 상수
 */
@UtilityClass
public class OceanConstants {

    // OCEAN 모델 Actor ID 매핑 (data.sql에 정의된 SYSTEM Actor)
    public static final Map<String, Integer> OCEAN_TYPE_TO_ACTOR_ID = Map.of(
            "Openness", 1,
            "Conscientiousness", 2,
            "Extraversion", 3,
            "Agreeableness", 4,
            "Neuroticism", 5
    );

    // Actor ID → OCEAN 타입 매핑
    public static final Map<Integer, OceanInfo> ACTOR_ID_TO_OCEAN_INFO = Map.of(
            1, new OceanInfo("OPENNESS", "개방성"),
            2, new OceanInfo("CONSCIENTIOUSNESS", "성실성"),
            3, new OceanInfo("EXTRAVERSION", "외향성"),
            4, new OceanInfo("AGREEABLENESS", "친화성"),
            5, new OceanInfo("NEUROTICISM", "신경성")
    );

    // Big5 점수 키 정규화 매핑 (AI 응답의 다양한 형태 → DB 키)
    public static final Map<String, String> BIG5_KEY_NORMALIZER = Map.of(
            "개방성", "openness",
            "성실성", "conscientiousness",
            "외향성", "extraversion",
            "친화성", "agreeableness",
            "신경성", "neuroticism",
            "openness", "openness",
            "conscientiousness", "conscientiousness",
            "extraversion", "extraversion",
            "agreeableness", "agreeableness",
            "neuroticism", "neuroticism"
    );

    /**
     * OCEAN 정보 (영문명, 한글명)
     */
    public static class OceanInfo {
        private final String englishName;
        private final String koreanName;

        public OceanInfo(String englishName, String koreanName) {
            this.englishName = englishName;
            this.koreanName = koreanName;
        }

        public String getEnglishName() {
            return englishName;
        }

        public String getKoreanName() {
            return koreanName;
        }
    }
}
