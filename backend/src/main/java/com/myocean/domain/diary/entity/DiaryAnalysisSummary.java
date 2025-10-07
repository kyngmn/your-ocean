package com.myocean.domain.diary.entity;

import com.myocean.global.common.BaseRDBEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "diary_analysis_summary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"diary"})
@EqualsAndHashCode(of = "id")
public class DiaryAnalysisSummary extends BaseRDBEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "diary_id", nullable = false)
    private Integer diaryId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "big5_scores", nullable = false, columnDefinition = "jsonb")
    private Map<String, Double> big5Scores;

    @Column(name = "domain_classification", length = 50)
    private String domainClassification;

    @Column(name = "final_conclusion", columnDefinition = "TEXT")
    private String finalConclusion;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "keywords", columnDefinition = "jsonb")
    private List<String> keywords;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", insertable = false, updatable = false)
    private Diary diary;
}