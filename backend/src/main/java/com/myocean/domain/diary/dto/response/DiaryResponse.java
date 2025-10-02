package com.myocean.domain.diary.dto.response;

import com.myocean.domain.diary.entity.Diary;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "다이어리 응답")
public class DiaryResponse {

    @Schema(description = "다이어리 ID", example = "1")
    private Integer id;

    @Schema(description = "사용자 ID", example = "1")
    private Integer userId;

    @Schema(description = "다이어리 제목", example = "오늘의 하루")
    private String title;

    @Schema(description = "다이어리 내용", example = "오늘은 정말 좋은 하루였다...")
    private String content;

    @Schema(description = "다이어리 날짜", example = "2024-01-15")
    private LocalDate diaryDate;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

}