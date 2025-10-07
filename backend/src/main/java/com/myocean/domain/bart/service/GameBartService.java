package com.myocean.domain.bart.service;

import com.myocean.domain.bart.dto.request.GameBartClickRequest;
import com.myocean.domain.bart.dto.request.GameBartFinishRoundRequest;
import com.myocean.domain.bart.dto.response.GameBartClickResponse;
import com.myocean.domain.bart.dto.response.GameBartRoundResponse;

public interface GameBartService {

    /**
     * BART 게임에서 풍선 펌프 클릭을 기록합니다.
     * 첫 번째 클릭인 경우 새로운 라운드를 생성합니다.
     *
     * @param userId     사용자 ID
     * @param sessionId  게임 세션 ID
     * @param roundIndex 라운드 번호 (0부터 시작)
     * @param request    클릭 요청 데이터
     * @return 클릭 기록 응답
     */
    GameBartClickResponse recordClick(Integer userId, Long sessionId, Integer roundIndex, GameBartClickRequest request);

    /**
     * BART 게임의 라운드를 종료합니다.
     *
     * @param userId     사용자 ID
     * @param sessionId  게임 세션 ID
     * @param roundIndex 라운드 번호 (0부터 시작)
     * @param request    라운드 종료 요청 데이터
     * @return 라운드 종료 응답
     */
    GameBartRoundResponse finishRound(Integer userId, Long sessionId, Integer roundIndex, GameBartFinishRoundRequest request);

    /**
     * BART 게임 세션 종료 시 결과 계산 및 저장
     *
     * @param sessionId 게임 세션 ID
     */
    void calculateAndSaveBartResults(Long sessionId);
}