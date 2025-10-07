package com.myocean.global.openai.dailymessage.dto;

import com.myocean.global.enums.BigCode;
import lombok.Getter;

@Getter
public class DailyMessageResponse {

    private String trait;           // 선택된 특성 (O, C, E, A, N)
    private String traitName;       // 특성 이름 (개방성, 성실성, 외향성, 친화성, 신경성)
    private String message;         // 오늘의 말
    private String timeSlot;        // 시간대

    public DailyMessageResponse(BigCode bigCode, String message, String timeSlot) {
        this.trait = bigCode.name();
        this.traitName = bigCode.getKoreanName();
        this.message = message;
        this.timeSlot = timeSlot;
    }
}