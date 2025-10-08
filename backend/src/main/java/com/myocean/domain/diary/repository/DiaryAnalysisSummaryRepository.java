package com.myocean.domain.diary.repository;

import com.myocean.domain.diary.entity.DiaryAnalysisSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiaryAnalysisSummaryRepository extends JpaRepository<DiaryAnalysisSummary, Long> {

    Optional<DiaryAnalysisSummary> findByDiaryId(Integer diaryId);

    void deleteByDiaryId(Integer diaryId);
}