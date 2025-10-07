package com.myocean.domain.survey.repository;

import com.myocean.domain.survey.entity.SurveyAnswer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Integer> {

    @EntityGraph(attributePaths = {"survey"})
    List<SurveyAnswer> findByUserId(Integer userId);

    boolean existsByUserId(Integer userId);

    @Modifying
    @Query("DELETE FROM SurveyAnswer sa WHERE sa.user.id = :userId")
    void deleteByUserId(@Param("userId") Integer userId);
}