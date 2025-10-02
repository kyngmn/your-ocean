package com.myocean.domain.big5.dto.response;

import com.myocean.domain.big5.enums.Big5SourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Big5ResultResponse {
    private Long id;
    private Integer userId;
    private Big5SourceType sourceType;
    private Long sourceId;
    private Integer resultO;
    private Integer resultC;
    private Integer resultE;
    private Integer resultA;
    private Integer resultN;
    private LocalDateTime computedAt;

    public Big5ScoresResponse toScoresResponse() {
        return Big5ScoresResponse.builder()
                .openness(resultO)
                .conscientiousness(resultC)
                .extraversion(resultE)
                .agreeableness(resultA)
                .neuroticism(resultN)
                .build();
    }
}