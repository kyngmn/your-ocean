package com.myocean.domain.report.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.myocean.domain.report.dto.converter.ReportConverter;
import com.myocean.domain.report.dto.response.ReportResponse;
import com.myocean.domain.report.entity.Report;
import com.myocean.domain.report.enums.ReportType;
import com.myocean.domain.report.repository.ReportRepository;
import com.myocean.response.exception.GeneralException;
import com.myocean.response.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Long saveFinalReport(Integer userId, Object averages) {
        String json = buildFinalReportJson(averages);
        Report saved = reportRepository.save(
                Report.builder()
                        .userId(userId)
                        .reportType(ReportType.FINAL)
                        .content(json)
                        .build()
        );
        return saved.getId();
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
            meta.put("version", "2.0");

            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.REPORT_NOT_FOUND);
        }
    }

    private String buildFinalReportJson(Object averages) {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            ObjectNode scores = root.putObject("bigFiveScores");

            // reflection으로 averages 객체의 필드 값 추출
            java.lang.reflect.Field oField = averages.getClass().getDeclaredField("o");
            java.lang.reflect.Field cField = averages.getClass().getDeclaredField("c");
            java.lang.reflect.Field eField = averages.getClass().getDeclaredField("e");
            java.lang.reflect.Field aField = averages.getClass().getDeclaredField("a");
            java.lang.reflect.Field nField = averages.getClass().getDeclaredField("n");

            oField.setAccessible(true);
            cField.setAccessible(true);
            eField.setAccessible(true);
            aField.setAccessible(true);
            nField.setAccessible(true);

            scores.put("O", (Integer) oField.get(averages));
            scores.put("C", (Integer) cField.get(averages));
            scores.put("E", (Integer) eField.get(averages));
            scores.put("A", (Integer) aField.get(averages));
            scores.put("N", (Integer) nField.get(averages));

            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.REPORT_NOT_FOUND);
        }
    }

}