package com.myocean.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BigCode {
    O("개방성", "Openness"),
    C("성실성", "Conscientiousness"),
    E("외향성", "Extraversion"),
    A("친화성", "Agreeableness"),
    N("신경성", "Neuroticism");

    private final String koreanName;
    private final String englishName;

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
}