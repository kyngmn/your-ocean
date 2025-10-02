package com.myocean.domain.report.repository;

import com.myocean.domain.report.entity.Report;
import com.myocean.domain.report.enums.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    Optional<Report> findByUserIdAndReportType(Integer userId, ReportType reportType);

    // 성능 최적화: 특정 사용자의 모든 리포트를 효율적으로 삭제
    void deleteByUserId(Integer userId);
}