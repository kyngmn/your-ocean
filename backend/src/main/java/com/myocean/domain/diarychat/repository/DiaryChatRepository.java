package com.myocean.domain.diarychat.repository;

import com.myocean.domain.diarychat.entity.DiaryChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryChatRepository extends JpaRepository<DiaryChatMessage, Long> {

    @Query("SELECT d FROM DiaryChatMessage d WHERE d.diaryId = :diaryId ORDER BY d.createdAt ASC")
    Page<DiaryChatMessage> findByDiaryIdOrderByCreatedAtAsc(@Param("diaryId") Integer diaryId, Pageable pageable);

    @Query("SELECT d FROM DiaryChatMessage d WHERE d.diaryId = :diaryId ORDER BY d.createdAt ASC")
    List<DiaryChatMessage> findByDiaryIdOrderByCreatedAtAsc(@Param("diaryId") Integer diaryId);

    @Query("SELECT COUNT(d) FROM DiaryChatMessage d WHERE d.diaryId = :diaryId")
    Long countByDiaryId(@Param("diaryId") Integer diaryId);

    @Query("SELECT d FROM DiaryChatMessage d JOIN d.diary diary WHERE diary.userId = :userId ORDER BY d.createdAt DESC")
    Page<DiaryChatMessage> findByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId, Pageable pageable);
}