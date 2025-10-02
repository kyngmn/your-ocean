package com.myocean.domain.diary.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "다이어리 생성 요청")
public class DiaryCreateRequest {

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 150, message = "제목은 150자 이하여야 합니다")
    @Schema(description = "다이어리 제목", example = "오늘의 하루")
    private String title;

    @NotBlank(message = "내용은 필수입니다")
    @Schema(description = "다이어리 내용", example = "오늘은 정말 좋은 하루였다...")
    private String content;

    @NotNull(message = "날짜는 필수입니다")
    @Schema(description = "다이어리 날짜", example = "2024-01-15")
    private LocalDate diaryDate;
}