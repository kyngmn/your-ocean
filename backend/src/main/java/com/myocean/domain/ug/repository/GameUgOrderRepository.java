package com.myocean.domain.ug.repository;

import com.myocean.domain.ug.entity.GameUgOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameUgOrderRepository extends JpaRepository<GameUgOrder, Long> {

    /**
     * Day별 Order 조회 (ID 범위 기반)
     * Day 1: 1-30, Day 2: 31-60, Day 3: 61-90
     *
     * @param startId 시작 ID
     * @param endId 끝 ID
     * @return Order 목록
     */
    @Query("SELECT o FROM GameUgOrder o WHERE o.id >= :startId AND o.id <= :endId ORDER BY o.id")
    List<GameUgOrder> findByIdRange(@Param("startId") Long startId, @Param("endId") Long endId);
}