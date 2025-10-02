package com.myocean.domain.diary.repository;

import com.myocean.domain.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Integer> {

    @Query("SELECT d FROM Diary d WHERE d.userId = :userId AND d.diaryDate BETWEEN :startDate AND :endDate AND d.deletedAt IS NULL ORDER BY d.diaryDate DESC")
    List<Diary> findByUserIdAndDateRangeAndNotDeleted(@Param("userId") Integer userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT d FROM Diary d WHERE d.userId = :userId AND d.diaryDate = :diaryDate AND d.deletedAt IS NULL")
    Optional<Diary> findByUserIdAndDiaryDateAndNotDeleted(@Param("userId") Integer userId, @Param("diaryDate") LocalDate diaryDate);

    @Query("SELECT d FROM Diary d WHERE d.id = :id AND d.userId = :userId AND d.deletedAt IS NULL")
    Optional<Diary> findByIdAndUserIdAndNotDeleted(@Param("id") Integer id, @Param("userId") Integer userId);

    // 성능 최적화: 특정 사용자의 모든 일기를 효율적으로 삭제
    void deleteByUserId(Integer userId);
}