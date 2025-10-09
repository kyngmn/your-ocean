package com.myocean.domain.diary.service;

import com.myocean.domain.diary.enums.AnalysisStatus;
import com.myocean.domain.diary.util.DiaryAnalysisParser;
import com.myocean.domain.diary.dto.response.DiaryAnalysisResponse;
import com.myocean.domain.diary.entity.DiaryAnalysisSummary;
import com.myocean.domain.diary.repository.DiaryAnalysisSummaryRepository;
import com.myocean.domain.diary.repository.DiaryRepository;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 다이어리 분석 요약 정보 저장/조회 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DiaryAnalysisSummaryService {

    private final DiaryAnalysisSummaryRepository diaryAnalysisSummaryRepository;
    private final DiaryRepository diaryRepository;

    /**
     * 초기 Summary 생성 (PROCESSING 상태)
     * 다이어리 생성 시 호출되어 분석 실패 시에도 상태 업데이트 가능하도록 함
     */
    @Transactional
    public void createInitialSummary(Integer diaryId) {
        log.info("초기 DiaryAnalysisSummary 생성 - diaryId: {}", diaryId);

        DiaryAnalysisSummary summary = DiaryAnalysisSummary.builder()
                .diary(diaryRepository.getReferenceById(diaryId))
                .big5Scores(new HashMap<>())
                .domainClassification("UNKNOWN")
                .finalConclusion("분석 진행 중")
                .keywords(new ArrayList<>())
                .status(AnalysisStatus.PROCESSING)
                .build();

        diaryAnalysisSummaryRepository.save(summary);
    }

    /**
     * DiaryAnalysisSummary 저장 (big5_scores, domain_classification, final_conclusion, keywords)
     */
    @Transactional
    public void saveDiaryAnalysisSummary(Integer diaryId, Map<String, Object> analysisData) {
        log.info("DiaryAnalysisSummary 업데이트 시작 - diaryId: {}", diaryId);

        // 기존 Summary 조회 (없으면 에러 - createDiary에서 미리 생성했어야 함)
        DiaryAnalysisSummary summary = diaryAnalysisSummaryRepository.findByDiaryId(diaryId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DIARY_ANALYSIS_SUMMARY_NOT_FOUND));

        // 파싱
        Map<String, Double> big5Scores = DiaryAnalysisParser.parseBig5Scores(analysisData);
        String domainClassification = DiaryAnalysisParser.parseDomainClassification(analysisData);
        String finalConclusion = DiaryAnalysisParser.parseFinalConclusion(analysisData);
        List<String> keywords = DiaryAnalysisParser.parseKeywords(analysisData);

        // 기존 Summary 업데이트
        summary.updateAnalysisData(big5Scores, domainClassification, finalConclusion, keywords);

        log.info("DiaryAnalysisSummary 업데이트 완료 - diaryId: {}, domain: {}, big5Keys: {}, keywords: {}",
                diaryId, domainClassification, big5Scores.keySet(), keywords);
    }

    /**
     * 다이어리 분석 요약 정보 조회
     */
    public DiaryAnalysisResponse.AnalysisSummary getAnalysisSummary(Integer diaryId) {
        DiaryAnalysisSummary summary = diaryAnalysisSummaryRepository.findByDiaryId(diaryId)
                .orElse(null);

        if (summary == null) {
            return createDefaultSummary();
        }

        return DiaryAnalysisResponse.AnalysisSummary.builder()
                .big5Scores(summary.getBig5Scores() != null ? summary.getBig5Scores() : new HashMap<>())
                .domainClassification(summary.getDomainClassification())
                .finalConclusion(summary.getFinalConclusion())
                .keywords(summary.getKeywords() != null ? summary.getKeywords() : new ArrayList<>())
                .build();
    }

    private DiaryAnalysisResponse.AnalysisSummary createDefaultSummary() {
        return DiaryAnalysisResponse.AnalysisSummary.builder()
                .big5Scores(new HashMap<>())
                .domainClassification("UNKNOWN")
                .finalConclusion("분석 결과를 불러올 수 없습니다.")
                .keywords(new ArrayList<>())
                .build();
    }

    /**
     * 분석 상태 조회
     */
    public AnalysisStatus getAnalysisStatus(Integer diaryId) {
        return diaryAnalysisSummaryRepository.findByDiaryId(diaryId)
                .map(DiaryAnalysisSummary::getStatus)
                .orElse(AnalysisStatus.PROCESSING); // Summary가 없으면 PROCESSING으로 간주
    }

    /**
     * 분석 상태 업데이트
     */
    @Transactional
    public void updateAnalysisStatus(Integer diaryId, AnalysisStatus status) {
        DiaryAnalysisSummary summary = diaryAnalysisSummaryRepository.findByDiaryId(diaryId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DIARY_ANALYSIS_SUMMARY_NOT_FOUND));

        summary.updateStatus(status);
        log.info("DiaryAnalysisSummary 상태 업데이트 - diaryId: {}, status: {}", diaryId, status);
    }
}
