package com.myocean.domain.survey.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyListResponse {

    private List<SurveyResponse> surveys;
    private Integer currentPage;
    private Integer totalPages;
    private Boolean hasNext;
}