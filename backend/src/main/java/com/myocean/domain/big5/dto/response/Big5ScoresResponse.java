package com.myocean.domain.big5.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Big5ScoresResponse {
    private Integer openness;
    private Integer conscientiousness;
    private Integer extraversion;
    private Integer agreeableness;
    private Integer neuroticism;
}