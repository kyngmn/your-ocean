package com.myocean.domain.diary.service;

import com.myocean.domain.diary.enums.AnalysisStatus;
import com.myocean.domain.diary.util.DiaryAnalysisParser;
import com.myocean.domain.diary.entity.DiaryAnalysisMessage;
import com.myocean.domain.diary.dto.response.DiaryAnalysisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 다이어리 분석 Orchestration 서비스
 * 파싱, 메시지 저장, 요약 저장을 조율
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DiaryAnalysisService {

    private final DiaryAnalysisMessageService messageService;
    private final DiaryAnalysisSummaryService summaryService;

    /**
     * AI 서버에서 받은 분석 결과를 파싱하고 DB에 저장
     * @param userId 사용자 ID
     * @param diaryId 다이어리 ID
     * @param analysisResult AI 서버에서 받은 분석 결과
     */
    @Transactional
    public void parseAndSaveAnalysisResult(Integer userId, Integer diaryId, Map<String, Object> analysisResult) {
        log.info("AI 분석 결과 파싱 시작 - userId: {}, diaryId: {}", userId, diaryId);

        if (analysisResult == null) {
            log.error("분석 결과가 null - userId: {}, diaryId: {}", userId, diaryId);
            return;
        }

        Map<String, Object> actualData = DiaryAnalysisParser.extractActualData(analysisResult);
        Map<String, Object> agentResponses = DiaryAnalysisParser.extractAgentResponses(actualData, userId, diaryId);

        if (agentResponses == null) {
            return;
        }

        messageService.saveOceanAnalysisMessages(userId, diaryId, agentResponses);
        summaryService.saveDiaryAnalysisSummary(diaryId, actualData);

        log.info("AI 분석 결과 파싱 및 저장 완료 - userId: {}, diaryId: {}", userId, diaryId);
    }

    /**
     * 저장된 OCEAN 분석 메시지들 조회
     */
    public List<DiaryAnalysisMessage> getStoredAnalysisMessages(Integer userId, Integer diaryId) {
        return messageService.getStoredAnalysisMessages(userId, diaryId);
    }

    /**
     * 다이어리 분석 요약 정보 조회
     */
    public DiaryAnalysisResponse.AnalysisSummary getAnalysisSummary(Integer diaryId) {
        return summaryService.getAnalysisSummary(diaryId);
    }

    /**
     * 분석 상태를 COMPLETED로 업데이트
     */
    @Transactional
    public void markAnalysisAsCompleted(Integer diaryId) {
        summaryService.updateAnalysisStatus(diaryId, AnalysisStatus.COMPLETED);
    }

    /**
     * 분석 상태를 FAILED로 업데이트
     */
    @Transactional
    public void markAnalysisAsFailed(Integer diaryId) {
        summaryService.updateAnalysisStatus(diaryId, AnalysisStatus.FAILED);
    }
}