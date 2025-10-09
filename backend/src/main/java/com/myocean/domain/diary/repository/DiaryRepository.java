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

    @Query("SELECT d FROM Diary d JOIN FETCH d.user WHERE d.user.id = :userId AND d.diaryDate BETWEEN :startDate AND :endDate AND d.deletedAt IS NULL ORDER BY d.diaryDate DESC")
    List<Diary> findByUserIdAndDateRangeAndNotDeleted(@Param("userId") Integer userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT d FROM Diary d JOIN FETCH d.user WHERE d.user.id = :userId AND d.diaryDate = :diaryDate AND d.deletedAt IS NULL")
    Optional<Diary> findByUserIdAndDiaryDateAndNotDeleted(@Param("userId") Integer userId, @Param("diaryDate") LocalDate diaryDate);

    @Query("SELECT d FROM Diary d JOIN FETCH d.user WHERE d.id = :id AND d.user.id = :userId AND d.deletedAt IS NULL")
    Optional<Diary> findByIdAndUserIdAndNotDeleted(@Param("id") Integer id, @Param("userId") Integer userId);

    @Query("SELECT COUNT(d) > 0 FROM Diary d WHERE d.user.id = :userId AND d.diaryDate = :diaryDate AND d.deletedAt IS NULL")
    boolean existsByUserIdAndDiaryDateAndNotDeleted(@Param("userId") Integer userId, @Param("diaryDate") LocalDate diaryDate);

}