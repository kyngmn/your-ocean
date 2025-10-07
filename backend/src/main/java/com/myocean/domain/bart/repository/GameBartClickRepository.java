package com.myocean.domain.bart.repository;

import com.myocean.domain.bart.entity.GameBartClick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameBartClickRepository extends JpaRepository<GameBartClick, Long> {

    /**
     * 특정 응답에 대한 클릭 개수를 조회합니다.
     *
     * @param responseId 응답 ID
     * @return 클릭 개수
     */
    Integer countByResponseId(Long responseId);

    /**
     * 특정 응답과 클릭 인덱스로 클릭 존재 여부를 확인합니다.
     *
     * @param responseId 응답 ID
     * @param clickIndex 클릭 인덱스
     * @return 존재 여부
     */
    boolean existsByResponseIdAndClickIndex(Long responseId, Integer clickIndex);

    /**
     * 여러 응답 ID에 해당하는 모든 클릭을 조회합니다.
     *
     * @param responseIds 응답 ID 목록
     * @return 클릭 목록
     */
    List<GameBartClick> findByResponseIdIn(List<Long> responseIds);
}