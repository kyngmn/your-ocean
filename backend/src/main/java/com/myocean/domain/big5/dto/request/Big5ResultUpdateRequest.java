package com.myocean.domain.big5.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Big5ResultUpdateRequest {

    @Min(0) @Max(100)
    private Integer openness;

    @Min(0) @Max(100)
    private Integer conscientiousness;

    @Min(0) @Max(100)
    private Integer extraversion;

    @Min(0) @Max(100)
    private Integer agreeableness;

    @Min(0) @Max(100)
    private Integer neuroticism;
}