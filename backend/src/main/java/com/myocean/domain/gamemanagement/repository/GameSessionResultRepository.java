package com.myocean.domain.gamemanagement.repository;

import com.myocean.domain.gamemanagement.entity.GameSessionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameSessionResultRepository extends JpaRepository<GameSessionResult, Long> {

    @Query("SELECT gsr FROM GameSessionResult gsr WHERE gsr.sessionId = :sessionId AND gsr.userId = :userId")
    Optional<GameSessionResult> findBySessionIdAndUserId(@Param("sessionId") Long sessionId, @Param("userId") Integer userId);

    @Query("SELECT gsr FROM GameSessionResult gsr JOIN gsr.gameSession gs WHERE gsr.sessionId = :sessionId AND gsr.userId = :userId")
    Optional<GameSessionResult> findBySessionIdAndUserIdWithSession(@Param("sessionId") Long sessionId, @Param("userId") Integer userId);

    // 성능 최적화: 특정 사용자의 모든 게임 결과를 효율적으로 삭제
    void deleteByUserId(Integer userId);
}