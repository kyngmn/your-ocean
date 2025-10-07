package com.myocean.domain.ug.service;

import com.myocean.domain.ug.dto.request.GameUgResponseRequest;
import com.myocean.domain.ug.dto.response.GameUgOrderResponse;
import com.myocean.domain.ug.dto.response.GameUgResponseDto;
import com.myocean.domain.ug.entity.GameUgResult;

import java.util.List;
import java.util.Optional;

public interface GameUgService {

    /**
     * UG 게임의 모든 오더 정보를 조회합니다.
     *
     * @return 오더 목록
     */
    List<GameUgOrderResponse> getGameOrders();

    /**
     * Day별 UG 게임 오더 정보를 조회합니다.
     * Day 1: Order ID 1-30, Day 2: Order ID 31-60, Day 3: Order ID 61-90
     *
     * @param day 게임 일차 (1, 2, 3)
     * @return Day별 오더 목록
     */
    List<GameUgOrderResponse> getGameOrdersByDay(Integer day);

    /**
     * UG 게임에서 제안 금액과 수락 여부를 제출합니다.
     *
     * @param userId 사용자 ID
     * @param sessionId 게임 세션 ID
     * @param roundId 라운드 ID
     * @param request 응답 요청 데이터
     * @return 응답 결과
     */
    GameUgResponseDto submitGameResponse(Integer userId, Long sessionId, Integer roundId, GameUgResponseRequest request);


    /**
     * 세션 기반 UG 게임 오더 정보를 조회합니다.
     * 사용자의 완료 횟수에 따라 자동으로 Day가 결정됩니다.
     *
     * @param sessionId 게임 세션 ID
     * @return 해당 세션에 맞는 30개 오더 목록
     */
    List<GameUgOrderResponse> getGameOrdersBySession(Long sessionId);

    /**
     * UG 게임 결과를 계산하고 저장합니다.
     *
     * @param sessionId 게임 세션 ID
     * @return 계산된 게임 결과
     */
    GameUgResult calculateAndSaveResult(Long sessionId);


}