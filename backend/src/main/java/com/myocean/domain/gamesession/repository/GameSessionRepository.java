package com.myocean.domain.gamesession.repository;

import com.myocean.domain.gamesession.entity.GameSession;
import com.myocean.domain.gamesession.enums.GameType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    Optional<GameSession> findByIdAndUser_Id(Long id, Integer userId);

    @Query("SELECT gs.gameType FROM GameSession gs WHERE gs.id = :sessionId AND gs.user.id = :userId")
    GameType findGameTypeBySessionId(@Param("sessionId") Long sessionId, @Param("userId") Integer userId);

    @Modifying
    @Query("DELETE FROM GameSession gs WHERE gs.user.id = :userId")
    void deleteByUserId(@Param("userId") Integer userId);

    @Query("SELECT COUNT(gs) FROM GameSession gs WHERE gs.user.id = :userId AND gs.gameType = :gameType AND gs.finishedAt IS NOT NULL")
    Long countCompletedGamesByUserIdAndGameType(@Param("userId") Integer userId, @Param("gameType") GameType gameType);
}