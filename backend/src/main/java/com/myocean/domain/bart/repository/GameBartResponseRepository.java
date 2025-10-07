package com.myocean.domain.bart.repository;

import com.myocean.domain.bart.entity.GameBartResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameBartResponseRepository extends JpaRepository<GameBartResponse, Long> {

    /**
     * 특정 세션과 라운드 번호로 BART 응답을 조회합니다.
     *
     * @param sessionId 세션 ID
     * @param roundIndex 라운드 번호
     * @return BART 응답
     */
    Optional<GameBartResponse> findBySessionIdAndRoundIndex(Long sessionId, Integer roundIndex);

    /**
     * 특정 세션의 모든 BART 응답을 조회합니다.
     *
     * @param sessionId 세션 ID
     * @return BART 응답 목록
     */
    List<GameBartResponse> findBySessionId(Long sessionId);

    /**
     * 특정 세션의 모든 BART 응답 개수를 조회합니다.
     *
     * @param sessionId 세션 ID
     * @return 응답 개수
     */
    long countBySessionId(Long sessionId);

    /**
     * 특정 세션의 완료된 BART 응답 개수를 조회합니다.
     *
     * @param sessionId 세션 ID
     * @return 완료된 응답 개수
     */
    int countBySessionIdAndFinishedAtIsNotNull(Long sessionId);
}