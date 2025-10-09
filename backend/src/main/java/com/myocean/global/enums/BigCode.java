package com.myocean.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BigCode {
    O("개방성", "Openness", 1L),
    C("성실성", "Conscientiousness", 2L),
    E("외향성", "Extraversion", 3L),
    A("친화성", "Agreeableness", 4L),
    N("신경성", "Neuroticism", 5L);

    private final String koreanName;
    private final String englishName;
    private final Long actorId;

    /**
     * 문자열로부터 BigCode enum을 찾습니다.
     * @param code enum 이름 (대소문자 무관)
     * @return 해당하는 BigCode
     * @throws IllegalArgumentException 유효하지 않은 코드인 경우
     */
    public static BigCode fromString(String code) {
        for (BigCode bigCode : BigCode.values()) {
            if (bigCode.name().equalsIgnoreCase(code)) {
                return bigCode;
            }
        }
        throw new IllegalArgumentException("Invalid BigCode: " + code);
    }

    /**
     * 영문 이름으로 Actor ID를 찾습니다.
     * @param englishName 영문 성격 이름 (예: "Openness")
     * @return 해당하는 Actor ID, 없으면 null
     */
    public static Long getActorIdByEnglishName(String englishName) {
        if (englishName == null) {
            return null;
        }

        for (BigCode code : values()) {
            if (code.englishName.equalsIgnoreCase(englishName)) {
                return code.actorId;
            }
        }
        return null;
    }

    /**
     * 영문 이름으로 BigCode enum을 찾습니다.
     * @param englishName 영문 성격 이름 (예: "Openness")
     * @return 해당하는 BigCode, 없으면 null
     */
    public static BigCode fromEnglishName(String englishName) {
        if (englishName == null) {
            return null;
        }

        for (BigCode code : values()) {
            if (code.englishName.equalsIgnoreCase(englishName)) {
                return code;
            }
        }
        return null;
    }

    /**
     * 한글 이름으로 BigCode enum을 찾습니다.
     * @param koreanName 한글 성격 이름 (예: "개방성")
     * @return 해당하는 BigCode, 없으면 null
     */
    public static BigCode fromKoreanName(String koreanName) {
        if (koreanName == null) {
            return null;
        }

        for (BigCode code : values()) {
            if (code.koreanName.equals(koreanName)) {
                return code;
            }
        }
        return null;
    }
}