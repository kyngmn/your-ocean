package com.myocean.domain.report.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.myocean.domain.report.dto.converter.ReportConverter;
import com.myocean.domain.report.dto.response.ReportResponse;
import com.myocean.domain.report.entity.Report;
import com.myocean.domain.report.enums.ReportType;
import com.myocean.domain.report.repository.ReportRepository;
import com.myocean.global.openai.personality.dto.PersonalityInsightsResponse;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final ObjectMapper objectMapper;

    public ReportResponse getSelfReport(Integer userId) {
        try {
            Report report = reportRepository.findByUserIdAndReportType(userId, ReportType.SELF)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.REPORT_NOT_FOUND));
            return ReportConverter.toResponse(report);
        } catch (org.springframework.dao.IncorrectResultSizeDataAccessException e) {
            log.warn("Multiple reports found for user {} and type SELF. Using the first one.", userId);
            // 중복 데이터가 있는 경우 첫 번째 것을 반환
            return reportRepository.findAll().stream()
                    .filter(r -> r.getUserId().equals(userId) && r.getReportType() == ReportType.SELF)
                    .findFirst()
                    .map(ReportConverter::toResponse)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.REPORT_NOT_FOUND));
        }
    }

    public ReportResponse getFinalReport(Integer userId) {
        try {
            Report report = reportRepository.findByUserIdAndReportType(userId, ReportType.FINAL)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.REPORT_NOT_FOUND));
            return ReportConverter.toResponse(report);
        } catch (org.springframework.dao.IncorrectResultSizeDataAccessException e) {
            log.warn("Multiple reports found for user {} and type FINAL. Using the first one.", userId);
            // 중복 데이터가 있는 경우 첫 번째 것을 반환
            return reportRepository.findAll().stream()
                    .filter(r -> r.getUserId().equals(userId) && r.getReportType() == ReportType.FINAL)
                    .findFirst()
                    .map(ReportConverter::toResponse)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.REPORT_NOT_FOUND));
        }
    }

    public Map<String, Integer> getSelfBig5Scores(Integer userId) {
        try {
            Report report = reportRepository.findByUserIdAndReportType(userId, ReportType.SELF)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.REPORT_NOT_FOUND));

            // Self 리포트 JSON 파싱
            JsonNode rootNode = objectMapper.readTree(report.getContent());
            JsonNode scoresNode = rootNode.get("bigFiveScores");

            if (scoresNode == null) {
                throw new GeneralException(ErrorStatus.REPORT_NOT_FOUND);
            }

            // 30개 세부 지표 점수 추출
            Map<String, Integer> scores = new HashMap<>();

            // 모든 필드를 순회하면서 점수 추출
            scoresNode.fieldNames().forEachRemaining(fieldName -> {
                scores.put(fieldName, scoresNode.get(fieldName).asInt());
            });

            return scores;

        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.REPORT_NOT_FOUND, e);
        }
    }

    @Transactional
    public Long saveSelfReport(Integer userId, Map<String, Integer> bigFiveScores) {
        String json = buildSelfReportJson(bigFiveScores);
        Report saved = reportRepository.save(
                Report.builder()
                        .userId(userId)
                        .reportType(ReportType.SELF)
                        .content(json)
                        .build()
        );
        return saved.getId();
    }


    @Transactional
    public void saveFinalReportWithInsights(Integer userId, Map<String, Integer> gameScores, PersonalityInsightsResponse insights) {
        try {
            // FINAL 리포트 JSON 구성 (기존 점수 + insights)
            ObjectNode rootNode = objectMapper.createObjectNode();

            // bigFiveScores 추가
            ObjectNode scoresNode = rootNode.putObject("bigFiveScores");
            gameScores.forEach(scoresNode::put);

            // headline 추가
            rootNode.put("headline", insights.getHeadline());

            // insights 추가
            ObjectNode insightsNode = rootNode.putObject("insights");
            insightsNode.put("main", insights.getInsights().getMain());
            insightsNode.put("gap", insights.getInsights().getGap());
            insightsNode.put("strength", insights.getInsights().getStrength());

            String jsonContent = objectMapper.writeValueAsString(rootNode);

            // Reports 테이블에 저장
            Report report = Report.builder()
                    .userId(userId)
                    .reportType(ReportType.FINAL)
                    .content(jsonContent)
                    .build();

            reportRepository.save(report);

        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR, e);
        }
    }


    private String buildSelfReportJson(Map<String, Integer> bigFiveScores) {
        try {
            ObjectNode root = objectMapper.createObjectNode();

            // BigFive 점수들을 저장
            ObjectNode scores = root.putObject("bigFiveScores");
            bigFiveScores.forEach(scores::put);

            // 메타데이터
            ObjectNode meta = root.putObject("meta");
            meta.put("totalQuestions", 120);
            meta.put("scaleMin", 1);
            meta.put("scaleMax", 5);
            meta.put("calculationMethod", "database_based");

            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.REPORT_NOT_FOUND);
        }
    }

}