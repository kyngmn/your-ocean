package com.myocean.domain.big5.dto.request;

import com.myocean.domain.big5.enums.Big5SourceType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Big5ResultCreateRequest {

    @NotNull
    private Big5SourceType sourceType;

    @NotNull
    private Long sourceId;

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