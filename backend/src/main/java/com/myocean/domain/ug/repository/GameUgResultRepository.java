package com.myocean.domain.ug.repository;

import com.myocean.domain.ug.entity.GameUgResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameUgResultRepository extends JpaRepository<GameUgResult, Long> {

    Optional<GameUgResult> findBySessionId(Long sessionId);

    boolean existsBySessionId(Long sessionId);
}