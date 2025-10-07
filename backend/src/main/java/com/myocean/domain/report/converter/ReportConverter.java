package com.myocean.domain.report.converter;

import com.myocean.domain.report.dto.response.ReportResponse;
import com.myocean.domain.report.entity.Report;

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