package com.myocean.domain.diary.repository;

import com.myocean.domain.diary.entity.DiaryAnalysisMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryAnalysisMessageRepository extends JpaRepository<DiaryAnalysisMessage, Long> {

    @Query("SELECT d FROM DiaryAnalysisMessage d WHERE d.diaryId = :diaryId ORDER BY d.createdAt ASC")
    Page<DiaryAnalysisMessage> findByDiaryIdOrderByCreatedAtAsc(@Param("diaryId") Integer diaryId, Pageable pageable);

    @Query("SELECT d FROM DiaryAnalysisMessage d WHERE d.diaryId = :diaryId ORDER BY d.createdAt ASC")
    List<DiaryAnalysisMessage> findByDiaryIdOrderByCreatedAtAsc(@Param("diaryId") Integer diaryId);

    @Query("SELECT COUNT(d) FROM DiaryAnalysisMessage d WHERE d.diaryId = :diaryId")
    Long countByDiaryId(@Param("diaryId") Integer diaryId);

    @Query("SELECT d FROM DiaryAnalysisMessage d JOIN d.diary diary WHERE diary.userId = :userId ORDER BY d.createdAt DESC")
    Page<DiaryAnalysisMessage> findByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId, Pageable pageable);
}