package com.myocean.domain.survey.service;

import com.myocean.domain.report.service.SelfReportAsyncService;
import com.myocean.domain.survey.dto.request.SurveyCompleteRequest;
import com.myocean.domain.survey.entity.SurveyAnswer;
import com.myocean.domain.survey.repository.SurveyRepository;
import com.myocean.domain.survey.repository.SurveyAnswerRepository;
import com.myocean.domain.user.repository.UserRepository;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SurveyAnswerService {

    private final SurveyAnswerRepository surveyAnswerRepository;
    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;
    private final SelfReportAsyncService selfReportAsyncService;

    @Transactional
    public void completeSurvey(Integer userId, SurveyCompleteRequest request) {
        // 1) 중복 제출 검증
        validateDuplicateSubmission(userId);

        // 2) 설문 응답 저장
        saveAnswers(userId, request);

        // 3) 빅파이브 점수 계산 및 리포트 생성 (비동기)
        selfReportAsyncService.calculateAndSaveReport(userId);
    }

    /**
     * 중복 제출 검증
     */
    private void validateDuplicateSubmission(Integer userId) {
        boolean alreadySubmitted = surveyAnswerRepository.existsByUserId(userId);
        if (alreadySubmitted) {
            log.warn("중복 설문 제출 시도 - userId: {}", userId);
            throw new GeneralException(ErrorStatus.SURVEY_ALREADY_SUBMITTED);
        }
    }

    /**
     * 설문 응답 저장
     */
    private void saveAnswers(Integer userId, SurveyCompleteRequest request) {
        LocalDateTime startedAt = LocalDateTime.now();

        try {
            List<SurveyAnswer> surveyAnswers = request.getResponses().stream()
                    .map(answer -> SurveyAnswer.builder()
                            .survey(surveyRepository.getReferenceById(answer.getSurveyId().shortValue()))
                            .user(userRepository.getReferenceById(userId))
                            .value(answer.getValue().shortValue())
                            .startedAt(startedAt)
                            .build())
                    .toList();

            surveyAnswerRepository.saveAll(surveyAnswers);
            log.info("설문 응답 저장 완료 - userId: {}, 응답 수: {}", userId, surveyAnswers.size());

        } catch (DataAccessException e) {
            log.error("설문 응답 DB 저장 실패 - userId: {}", userId, e);
            throw new GeneralException(ErrorStatus.SURVEY_RESPONSE_SAVE_FAILED, e);
        } catch (Exception e) {
            log.error("설문 응답 저장 중 예상치 못한 오류 - userId: {}", userId, e);
            throw new GeneralException(ErrorStatus.SURVEY_RESPONSE_SAVE_FAILED, e);
        }
    }
}
