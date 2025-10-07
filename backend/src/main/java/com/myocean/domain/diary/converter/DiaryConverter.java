package com.myocean.domain.diary.converter;

import com.myocean.domain.diary.dto.response.DiaryResponse;
import com.myocean.domain.diary.entity.Diary;

public class DiaryConverter {

    public static DiaryResponse toResponse(Diary diary) {
        return DiaryResponse.builder()
                .id(diary.getId())
                .userId(diary.getUserId())
                .title(diary.getTitle())
                .content(diary.getContent())
                .diaryDate(diary.getDiaryDate())
                .createdAt(diary.getCreatedAt())
                .updatedAt(diary.getUpdatedAt())
                .build();
    }
}