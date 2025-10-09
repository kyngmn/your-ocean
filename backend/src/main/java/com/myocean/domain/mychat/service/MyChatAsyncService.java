package com.myocean.domain.mychat.service;

import com.myocean.global.ai.AiClientService;
import com.myocean.global.openai.chatanalysis.service.ChatAnalysisRefinementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyChatAsyncService {

    private final AiClientService aiClientService;
    private final ChatAnalysisRefinementService chatAnalysisRefinementService;
    private final MyChatAnalysisService myChatAnalysisService;

    @Async
    @Retryable(
            retryFor = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void asyncAnalyzeMyChat(Integer userId, Long messageId, String message) {
        log.info("[ASYNC] 비동기 AI 분석 시작 - messageId: {}, thread: {}",
                messageId, Thread.currentThread().getName());

        // AI 서버 호출
        Map<String, Object> analysisResult = aiClientService.analyzeChatMessage(userId, messageId, message);

        // OpenAI로 분석 결과 다듬기
        Map<String, Object> refinedResult = chatAnalysisRefinementService.refineChatAnalysisResult(
                message, analysisResult);

        // 다듬어진 분석 결과를 파싱해서 메시지로 저장 + SSE 전송
        myChatAnalysisService.parseAndSaveAnalysisResult(userId, messageId, refinedResult);

        // 분석 성공 시 상태를 COMPLETED로 업데이트
        myChatAnalysisService.markAnalysisAsCompleted(messageId);

        log.info("[ASYNC] 비동기 AI 분석 및 저장 완료 - messageId: {}", messageId);
    }

    /**
     * 재시도 3번 모두 실패 시 호출되는 복구 메서드
     */
    @Recover
    public void recoverAnalysisFailed(Exception e, Integer userId, Long messageId, String message) {
        log.error("[ASYNC] 비동기 AI 분석 최종 실패 - messageId: {}, error: {}", messageId, e.getMessage(), e);

        // 분석 실패 시 상태를 FAILED로 업데이트
        try {
            myChatAnalysisService.markAnalysisAsFailed(messageId);
        } catch (Exception ex) {
            log.error("[ASYNC] 분석 실패 상태 업데이트 실패 - messageId: {}", messageId, ex);
        }
    }
}
