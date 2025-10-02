package com.myocean.domain.survey.repository;

import com.myocean.domain.survey.entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Integer> {

    @Query("SELECT sr FROM SurveyResponse sr JOIN FETCH sr.survey s JOIN FETCH s.bigFiveCode WHERE sr.user.id = :userId")
    List<SurveyResponse> findByUserIdWithSurvey(@Param("userId") Integer userId);

    // 성능 최적화: 특정 사용자의 모든 설문 응답을 효율적으로 삭제
    @Modifying
    @Query("DELETE FROM SurveyResponse sr WHERE sr.user.id = :userId")
    void deleteByUserId(@Param("userId") Integer userId);
}