package com.myocean.domain.report.service;

import com.myocean.domain.survey.calculator.SurveyResultCalculator;
import com.myocean.domain.survey.entity.SurveyAnswer;
import com.myocean.domain.survey.repository.SurveyAnswerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SelfReportAsyncService {

    private final SurveyResultCalculator surveyResultCalculator;
    private final SurveyAnswerRepository surveyAnswerRepository;
    private final ReportService reportService;

    /**
     * 빅파이브 점수 계산 및 리포트 생성 (비동기, 재시도)
     * 최대 3번 재시도, 각 재시도 간 2초 대기 (exponential backoff)
     */
    @Async
    @Retryable(
        retryFor = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void calculateAndSaveReport(Integer userId) {
        try {
            log.info("Big5 점수 계산 시작 - userId: {}", userId);

            // 1) 설문 응답 조회
            List<SurveyAnswer> responses = surveyAnswerRepository.findByUserId(userId);
            log.info("설문 응답 조회 완료 - userId: {}, 응답 수: {}", userId, responses.size());

            // 2) 빅파이브 점수 계산
            Map<String, Integer> bigFiveScores = surveyResultCalculator.calculateBigFiveScores(responses);

            // 3) 리포트 생성 및 저장
            reportService.saveSelfReport(userId, bigFiveScores);

            log.info("Big5 점수 계산 및 리포트 생성 완료 - userId: {}", userId);

        } catch (Exception e) {
            log.error("Big5 점수 계산 실패 (재시도 예정) - userId: {}, error: {}", userId, e.getMessage());
            throw e;  // 재시도를 위해 예외를 다시 던짐
        }
    }

    /**
     * 재시도 실패 시 최종 처리 (Recover)
     * 3번 재시도 후에도 실패하면 이 메서드 호출
     */
    @Recover
    public void recoverCalculateAndSaveReport(Exception e, Integer userId) {
        log.error("Big5 점수 계산 최종 실패 (3번 재시도 후) - userId: {}, error: {}",
                  userId, e.getMessage(), e);

        // - 실패한 userId를 별도 테이블에 기록하여 수동 처리
        // - 예: failedCalculationRepository.save(new FailedCalculation(userId, e.getMessage()));
    }
}
