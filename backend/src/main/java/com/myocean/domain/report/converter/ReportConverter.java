package com.myocean.domain.report.converter;

import com.myocean.domain.report.dto.response.ReportResponse;
import com.myocean.domain.report.entity.Report;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReportConverter {

    public static ReportResponse toResponse(Report report) {
        return new ReportResponse(
                report.getId(),
                report.getUserId(),
                report.getReportType(),
                report.getContent(),
                report.getCreatedAt()
        );
    }
}