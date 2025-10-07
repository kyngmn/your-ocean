package com.myocean.domain.diary.service;

import com.myocean.domain.diary.entity.DiaryAnalysisMessage;
import com.myocean.domain.diary.repository.DiaryRepository;
import com.myocean.global.ai.AiClientService;
import com.myocean.global.openai.diaryanalysis.service.DiaryAnalysisRefinementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryAsyncService {

    private final DiaryRepository diaryRepository;
    private final AiClientService aiClientService;
    private final DiaryAnalysisService diaryAnalysisService;
    private final DiaryAnalysisRefinementService diaryAnalysisRefinementService;

    @Async
    @Transactional
    public void asyncAnalyzeDiary(Integer userId, Integer diaryId, String title, String content) {
        try {
            log.info("🔄 [ASYNC] 비동기 AI 분석 시작 - diaryId: {}, thread: {}",
                    diaryId, Thread.currentThread().getName());

            log.info("🔄 [ASYNC] AI 서버 호출 중 - diaryId: {}", diaryId);
            Map<String, Object> analysisResult = analyzeDiary(userId, diaryId, content, title);
            log.info("🔄 [ASYNC] AI 서버 응답 완료 - diaryId: {}", diaryId);

            // OpenAI로 분석 결과 다듬기
            log.info("🔄 [ASYNC] OpenAI 분석 결과 다듬기 시작 - diaryId: {}", diaryId);
            Map<String, Object> refinedResult = diaryAnalysisRefinementService.refineAnalysisResult(
                title, content, analysisResult);
            log.info("🔄 [ASYNC] OpenAI 분석 결과 다듬기 완료 - diaryId: {}", diaryId);

            // 다듬어진 분석 결과를 파싱해서 OCEAN 메시지로 저장
            log.info("🔄 [ASYNC] OCEAN 메시지 저장 시작 - diaryId: {}", diaryId);
            diaryAnalysisService.parseAndSaveAnalysisResult(userId, diaryId, refinedResult);

            log.info("✅ [ASYNC] 비동기 AI 분석 및 저장 완료 - diaryId: {}, thread: {}",
                    diaryId, Thread.currentThread().getName());
        } catch (Exception e) {
            log.error("❌ [ASYNC] 비동기 AI 분석 실패 - diaryId: {}, thread: {}, error: {}",
                    diaryId, Thread.currentThread().getName(), e.getMessage(), e);
        }
    }

    private Map<String, Object> analyzeDiary(Integer userId, Integer diaryId, String content, String title) {
        try {
            log.info("AI 서버로 다이어리 분석 요청 - userId: {}, diaryId: {}", userId, diaryId);
            Map<String, Object> analysisResult = aiClientService.analyzeDiary(userId, diaryId, content, title);
            return analysisResult;
        } catch (Exception e) {
            log.error("다이어리 분석 실패 - userId: {}, diaryId: {}, error: {}", userId, diaryId, e.getMessage(), e);
            throw new RuntimeException("다이어리 분석 중 오류가 발생했습니다.", e);
        }
    }
}