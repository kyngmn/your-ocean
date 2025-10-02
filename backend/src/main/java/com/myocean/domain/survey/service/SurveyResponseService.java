package com.myocean.domain.survey.service;

import com.myocean.domain.report.service.ReportService;
import com.myocean.domain.survey.dto.request.SurveyCompleteRequest;
import com.myocean.domain.survey.entity.Survey;
import com.myocean.domain.survey.entity.SurveyResponse;
import com.myocean.domain.survey.repository.SurveyRepository;
import com.myocean.domain.survey.repository.SurveyResponseRepository;
import com.myocean.domain.user.entity.User;
import com.myocean.domain.user.repository.UserRepository;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SurveyResponseService {

    private final SurveyResponseRepository surveyResponseRepository;
    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;
    private final SurveyCalculationService surveyCalculationService;
    private final ReportService reportService;

    @Transactional
    public void completeSurvey(Integer userId, SurveyCompleteRequest request) {
        LocalDateTime startedAt = LocalDateTime.now();

        try {
            // 1) 설문 응답 저장
            List<SurveyResponse> surveyResponses = request.getResponses().stream()
                    .map(answer -> SurveyResponse.builder()
                            .survey(surveyRepository.getReferenceById(answer.getSurveyId().shortValue()))
                            .user(userRepository.getReferenceById(userId))
                            .value(answer.getValue().shortValue())
                            .startedAt(startedAt)
                            .build())
                    .toList();

            surveyResponseRepository.saveAll(surveyResponses);

        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.SURVEY_RESPONSE_SAVE_FAILED, e);
        }

        try {
            // 2) 120문항 완료 후 빅파이브 점수 계산 및 리포트 생성
            Map<String, Integer> bigFiveScores = surveyCalculationService.calculateBigFiveScoresByUserId(userId);
            reportService.saveSelfReport(userId, bigFiveScores);

        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.SURVEY_CALCULATION_FAILED, e);
        }
    }
}