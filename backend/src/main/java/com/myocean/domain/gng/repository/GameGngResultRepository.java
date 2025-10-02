package com.myocean.domain.gng.repository;

import com.myocean.domain.gng.entity.GameGngResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameGngResultRepository extends JpaRepository<GameGngResult, Long> {
}