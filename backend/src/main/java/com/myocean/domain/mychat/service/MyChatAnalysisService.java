package com.myocean.domain.mychat.service;

import com.myocean.domain.big5.enums.Big5SourceType;
import com.myocean.domain.big5.service.Big5CalculationService;
import com.myocean.domain.diary.util.DiaryAnalysisParser;
import com.myocean.domain.mychat.entity.MyChatMessage;
import com.myocean.domain.mychat.repository.MyChatRepository;
import com.myocean.global.enums.AnalysisStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 채팅 분석 Orchestration 서비스
 * 파싱, 메시지 저장, SSE 전송을 조율
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MyChatAnalysisService {

    private final MyChatMessageService messageService;
    private final MyChatRepository myChatRepository;
    private final Big5CalculationService big5CalculationService;

    /**
     * AI 서버에서 받은 분석 결과를 파싱하고 저장 + SSE 전송
     * @param userId 사용자 ID
     * @param messageId 사용자 메시지 ID
     * @param analysisResult AI 서버에서 받은 분석 결과
     */
    @Transactional
    public void parseAndSaveAnalysisResult(Integer userId, Long messageId, Map<String, Object> analysisResult) {
        log.info("AI 분석 결과 파싱 시작 - userId: {}, messageId: {}", userId, messageId);

        if (analysisResult == null) {
            log.error("분석 결과가 null - userId: {}, messageId: {}", userId, messageId);
            return;
        }

        // 래핑된 응답에서 data 추출
        Map<String, Object> actualData = extractActualData(analysisResult);

        // agent_responses 추출
        Map<String, String> agentResponses = extractAgentResponses(actualData, userId, messageId);

        if (agentResponses == null || agentResponses.isEmpty()) {
            return;
        }

        // 상위 3개 성격 응답 저장
        List<MyChatMessage> savedMessages = messageService.saveTopThreePersonaResponses(userId, agentResponses);

        // Big5Result에도 저장
        saveBig5Result(userId, messageId, actualData);

        // SSE로 스트리밍
        messageService.streamMessages(userId, savedMessages);

        log.info("AI 분석 결과 파싱 및 저장 완료 - userId: {}, messageId: {}", userId, messageId);
    }

    /**
     * Big5 점수를 Big5Result 테이블에 저장
     */
    private void saveBig5Result(Integer userId, Long messageId, Map<String, Object> actualData) {
        try {
            Map<String, Double> big5Scores = DiaryAnalysisParser.parseBig5Scores(actualData);

            if (big5Scores == null || big5Scores.isEmpty()) {
                log.warn("Big5 점수가 없어서 Big5Result 저장 스킵 - messageId: {}", messageId);
                return;
            }

            big5CalculationService.saveBig5Result(userId, Big5SourceType.MY_CHAT, messageId, big5Scores);
        } catch (Exception e) {
            log.error("Big5Result 저장 실패 - userId: {}, messageId: {}, error: {}",
                    userId, messageId, e.getMessage(), e);
            // Big5Result 저장 실패해도 전체 프로세스는 계속 진행
        }
    }

    /**
     * AI 응답에서 실제 데이터 추출
     */
    private Map<String, Object> extractActualData(Map<String, Object> analysisResult) {
        if (analysisResult.containsKey("data") && analysisResult.get("data") instanceof Map) {
            return (Map<String, Object>) analysisResult.get("data");
        }
        return analysisResult;
    }

    /**
     * agent_responses 추출
     */
    private Map<String, String> extractAgentResponses(Map<String, Object> actualData, Integer userId, Long messageId) {
        Object agentResponsesObj = actualData.get("agent_responses");
        if (!(agentResponsesObj instanceof Map)) {
            log.error("agent_responses가 Map 타입이 아니거나 비어있음 - userId: {}, messageId: {}", userId, messageId);
            return null;
        }
        return (Map<String, String>) agentResponsesObj;
    }

    /**
     * 분석 상태를 COMPLETED로 업데이트
     */
    @Transactional
    public void markAnalysisAsCompleted(Long messageId) {
        myChatRepository.updateAnalysisStatus(messageId, AnalysisStatus.COMPLETED);
    }

    /**
     * 분석 상태를 FAILED로 업데이트
     */
    @Transactional
    public void markAnalysisAsFailed(Long messageId) {
        myChatRepository.updateAnalysisStatus(messageId, AnalysisStatus.FAILED);
    }
}
