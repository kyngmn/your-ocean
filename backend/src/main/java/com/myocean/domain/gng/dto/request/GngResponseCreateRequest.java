package com.myocean.domain.gng.dto.request;

import com.myocean.domain.gng.enums.GngStimulus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "GNG 라운드 응답 저장 요청")
public record GngResponseCreateRequest(
        @Schema(description = "자극 타입", example = "GO", allowableValues = {"GO", "NOGO"})
        @NotNull(message = "자극 타입은 필수")
        GngStimulus stimulusType,

        @Schema(description = "자극 시작 시간")
        @NotNull(message = "자극 시작 시간은 필수")
        LocalDateTime stimulusStartedAt,

        @Schema(description = "응답 시간 (응답하지 않은 경우 null)")
        LocalDateTime respondedAt,

        @Schema(description = "성공 여부 (true: 성공, false: 실패)")
        Boolean isSucceeded
) {
}