package com.myocean.domain.ug.repository;

import com.myocean.domain.ug.entity.GameUgResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameUgResponseRepository extends JpaRepository<GameUgResponse, Long> {

    /**
     * 특정 세션의 모든 UG 응답을 조회합니다.
     *
     * @param sessionId 세션 ID
     * @return UG 응답 목록
     */
    List<GameUgResponse> findBySessionId(Long sessionId);

    /**
     * 특정 세션의 UG 응답 개수를 조회합니다.
     *
     * @param sessionId 세션 ID
     * @return 응답 개수
     */
    long countBySessionId(Long sessionId);

    /**
     * 특정 세션의 모든 UG 응답을 라운드 순서로 조회합니다.
     *
     * @param sessionId 세션 ID
     * @return 라운드 순으로 정렬된 UG 응답 목록
     */
    List<GameUgResponse> findBySessionIdOrderByRoundAsc(Long sessionId);

    /**
     * 특정 세션의 특정 라운드에 응답이 존재하는지 확인합니다.
     *
     * @param sessionId 세션 ID
     * @param round 라운드 번호
     * @return 응답 존재 여부
     */
    boolean existsBySessionIdAndRound(Long sessionId, Integer round);
}