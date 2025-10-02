package com.myocean.domain.gng.repository;

import com.myocean.domain.gng.entity.GameGngResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameGngResponseRepository extends JpaRepository<GameGngResponse, Long> {

    List<GameGngResponse> findBySessionId(Long sessionId);
}