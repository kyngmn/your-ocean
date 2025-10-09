package com.myocean.domain.diary.service;

import com.myocean.domain.diary.converter.DiaryAnalysisConverter;
import com.myocean.domain.diary.dto.response.DiaryAnalysisResponse;
import com.myocean.domain.diary.entity.DiaryAnalysisMessage;
import com.myocean.domain.diary.enums.AnalysisStatus;
import com.myocean.response.ApiResponse;
import com.myocean.response.status.SuccessStatus;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DiaryAnalysisStreamService {

    private final DiaryAnalysisMessageService messageService;
    private final DiaryAnalysisSummaryService summaryService;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    private static final long TIMEOUT = 60000L; // 60초
    private static final long DELAY_BETWEEN_MESSAGES = 1000L; // 1초

    /**
     * SSE를 통해 분석 결과를 순차적으로 스트리밍
     */
    public SseEmitter streamAnalysisResult(Integer diaryId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);

        executor.execute(() -> {
            try {
                log.info("[SSE] 스트리밍 시작 - diaryId: {}", diaryId);

                // 0. 분석 상태 확인
                AnalysisStatus analysisStatus = summaryService.getAnalysisStatus(diaryId);

                // PROCESSING 상태면 처리 중 메시지 전송
                if (analysisStatus == AnalysisStatus.PROCESSING) {
                    ApiResponse<String> processingResponse = ApiResponse.onSuccess(
                            SuccessStatus.DIARY_ANALYSIS_PROCESSING,
                            "분석이 아직 진행 중입니다. 잠시 후 다시 시도해주세요."
                    );

                    emitter.send(SseEmitter.event()
                            .name("status")
                            .data(processingResponse));

                    emitter.complete();
                    log.info("[SSE] PROCESSING 상태 - diaryId: {}", diaryId);
                    return;
                }

                // FAILED 상태면 실패 메시지 전송
                if (analysisStatus == AnalysisStatus.FAILED) {
                    ApiResponse<String> failedResponse = ApiResponse.onSuccess(
                            SuccessStatus.DIARY_ANALYSIS_FAILED,
                            "분석이 실패했습니다."
                    );

                    emitter.send(SseEmitter.event()
                            .name("status")
                            .data(failedResponse));

                    emitter.complete();
                    log.info("[SSE] FAILED 상태 - diaryId: {}", diaryId);
                    return;
                }

                // 1. OCEAN 메시지 5개 순차 전송 (ApiResponse로 감싸기)
                List<DiaryAnalysisMessage> messages = messageService.getStoredAnalysisMessages(diaryId);

                for (DiaryAnalysisMessage message : messages) {
                    DiaryAnalysisResponse.OceanMessage oceanMessage = DiaryAnalysisConverter.toOceanMessage(message);

                    ApiResponse<DiaryAnalysisResponse.OceanMessage> response = ApiResponse.onSuccess(
                            SuccessStatus.DIARY_ANALYSIS_COMPLETED,
                            oceanMessage
                    );

                    emitter.send(SseEmitter.event()
                            .name("ocean-message")
                            .data(response));

                    log.info("[SSE] OCEAN 메시지 전송 - diaryId: {}, personality: {}",
                            diaryId, oceanMessage.getPersonality());

                    Thread.sleep(DELAY_BETWEEN_MESSAGES);
                }

                // 2. Summary 전송 (ApiResponse로 감싸기)
                DiaryAnalysisResponse.AnalysisSummary summary = summaryService.getAnalysisSummary(diaryId);

                ApiResponse<DiaryAnalysisResponse.AnalysisSummary> summaryResponse = ApiResponse.onSuccess(
                        SuccessStatus.DIARY_ANALYSIS_COMPLETED,
                        summary
                );

                emitter.send(SseEmitter.event()
                        .name("summary")
                        .data(summaryResponse));

                log.info("[SSE] Summary 전송 - diaryId: {}", diaryId);

                // 3. 완료 이벤트 전송
                ApiResponse<String> completeResponse = ApiResponse.onSuccess(
                        SuccessStatus.DIARY_ANALYSIS_COMPLETED,
                        "분석 스트리밍 완료"
                );

                emitter.send(SseEmitter.event()
                        .name("complete")
                        .data(completeResponse));

                emitter.complete();
                log.info("[SSE] 스트리밍 완료 - diaryId: {}", diaryId);

            } catch (IOException e) {
                log.error("[SSE] 전송 실패 - diaryId: {}", diaryId, e);
                emitter.completeWithError(e);
            } catch (InterruptedException e) {
                log.error("[SSE] 스트리밍 중단 - diaryId: {}", diaryId, e);
                Thread.currentThread().interrupt();
                emitter.completeWithError(e);
            } catch (Exception e) {
                log.error("[SSE] 예상치 못한 오류 - diaryId: {}", diaryId, e);
                emitter.completeWithError(e);
            }
        });

        emitter.onCompletion(() -> log.info("[SSE] Emitter 완료 - diaryId: {}", diaryId));
        emitter.onTimeout(() -> log.warn("[SSE] Emitter 타임아웃 - diaryId: {}", diaryId));
        emitter.onError(e -> log.error("[SSE] Emitter 에러 - diaryId: {}", diaryId, e));

        return emitter;
    }

    /**
     * 애플리케이션 종료 시 ExecutorService 정리
     */
    @PreDestroy
    public void shutdown() {
        log.info("[SSE] ExecutorService 종료 시작");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                log.warn("[SSE] ExecutorService가 10초 내에 종료되지 않아 강제 종료");
                executor.shutdownNow();
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.error("[SSE] ExecutorService 강제 종료 실패");
                }
            }
            log.info("[SSE] ExecutorService 정상 종료");
        } catch (InterruptedException e) {
            log.error("[SSE] ExecutorService 종료 중 인터럽트 발생");
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
