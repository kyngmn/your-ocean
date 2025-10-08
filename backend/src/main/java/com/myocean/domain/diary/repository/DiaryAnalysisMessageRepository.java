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

    @Query("SELECT d FROM DiaryAnalysisMessage d JOIN FETCH d.senderActor WHERE d.diary.id = :diaryId ORDER BY d.createdAt ASC")
    List<DiaryAnalysisMessage> findByDiaryIdOrderByCreatedAtAsc(@Param("diaryId") Integer diaryId);

}