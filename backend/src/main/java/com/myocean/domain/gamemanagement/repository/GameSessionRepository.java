package com.myocean.domain.gamemanagement.repository;

import com.myocean.domain.gamemanagement.entity.GameSession;
import com.myocean.domain.gamemanagement.enums.GameType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    @Query("SELECT gs FROM GameSession gs WHERE gs.id = :sessionId AND gs.userId = :userId")
    Optional<GameSession> findByIdAndUserId(@Param("sessionId") Long sessionId, @Param("userId") Integer userId);

    @Query("SELECT gs FROM GameSession gs WHERE gs.userId = :userId ORDER BY gs.startedAt DESC")
    List<GameSession> findByUserIdOrderByStartedAtDesc(@Param("userId") Integer userId);

    @Query("SELECT gs FROM GameSession gs WHERE gs.userId = :userId AND gs.gameType = :gameType ORDER BY gs.startedAt DESC")
    List<GameSession> findByUserIdAndGameTypeOrderByStartedAtDesc(@Param("userId") Integer userId, @Param("gameType") GameType gameType);

    @Query("SELECT gs FROM GameSession gs WHERE gs.userId = :userId AND gs.finishedAt IS NULL")
    List<GameSession> findUnfinishedSessionsByUserId(@Param("userId") Integer userId);

    @Query("SELECT gs FROM GameSession gs WHERE gs.id IN :ids")
    List<GameSession> findByIdIn(@Param("ids") List<Long> ids);

    @Query("SELECT gs.gameType FROM GameSession gs WHERE gs.id = :sessionId AND gs.userId = :userId")
    GameType findGameTypeBySessionId(@Param("sessionId") Long sessionId, @Param("userId") Integer userId);

    @Modifying
    @Query("DELETE FROM GameSession gs WHERE gs.userId = :userId")
    void deleteByUserId(@Param("userId") Integer userId);
}