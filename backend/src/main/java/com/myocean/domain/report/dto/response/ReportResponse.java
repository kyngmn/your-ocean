package com.myocean.domain.report.dto.response;

import com.myocean.domain.report.enums.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Report response")
public record ReportResponse(
        @Schema(description = "Report ID")
        Long id,

        @Schema(description = "User ID")
        Integer userId,

        @Schema(description = "Report type", example = "SELF")
        ReportType reportType,

        @Schema(description = "Report content (JSONB)")
        String content,

        @Schema(description = "Created time")
        LocalDateTime createdAt
) {
}