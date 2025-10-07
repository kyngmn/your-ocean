package com.myocean.domain.bart.repository;

import com.myocean.domain.bart.entity.GameBartResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameBartResultRepository extends JpaRepository<GameBartResult, Long> {
    
    boolean existsBySessionId(Long sessionId);
    
    Optional<GameBartResult> findBySessionId(Long sessionId);
}