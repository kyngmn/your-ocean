package com.myocean.domain.diary.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "다이어리 월별 작성 유무 응답")
public record DiaryCalendarResponse(
        @Schema(description = "조회 년월", example = "2024-02")
        String yearMonth,

        @Schema(description = "다이어리 작성된 날짜들", example = "[\"2024-02-01\", \"2024-02-15\", \"2024-02-28\"]")
        List<LocalDate> diaryDates
) {
    public static DiaryCalendarResponse of(String yearMonth, List<LocalDate> diaryDates) {
        return new DiaryCalendarResponse(yearMonth, diaryDates);
    }
}