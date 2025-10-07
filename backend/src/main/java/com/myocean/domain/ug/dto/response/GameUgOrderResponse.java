package com.myocean.domain.ug.dto.response;

import com.myocean.domain.ug.enums.MoneySize;
import com.myocean.domain.ug.enums.PersonaType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "UG 게임 오더 응답")
public record GameUgOrderResponse(
        @Schema(description = "오더 ID", example = "1")
        Long id,

        @Schema(description = "역할 타입", example = "1", allowableValues = {"1", "2", "3"})
        Integer roleType,

        @Schema(description = "페르소나 타입", example = "FAMILY")
        PersonaType personaType,

        @Schema(description = "금액 크기", example = "LARGE")
        MoneySize money,

        @Schema(description = "비율", example = "5")
        Integer rate,

        @Schema(description = "역할 설명", example = "제안자")
        String roleDescription
) {
    public static GameUgOrderResponse from(Long id, Integer roleType, PersonaType personaType,
                                         MoneySize money, Integer rate) {
        String roleDescription = switch (roleType) {
            case 1 -> "제안자";
            case 2 -> "응답자";
            case 3 -> "무조건 수락 제안자";
            default -> throw new IllegalArgumentException("유효하지 않은 역할 타입: " + roleType);
        };

        return new GameUgOrderResponse(id, roleType, personaType, money, rate, roleDescription);
    }
}