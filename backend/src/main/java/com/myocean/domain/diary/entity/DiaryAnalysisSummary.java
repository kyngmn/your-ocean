package com.myocean.domain.diary.entity;

import com.myocean.domain.diary.enums.AnalysisStatus;
import com.myocean.global.common.BaseRDBEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "diary_analysis_summary",
        indexes = {
                @Index(name = "idx_summary_diary_id", columnList = "diary_id", unique = true)
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"diary"})
@EqualsAndHashCode(of = "id", callSuper = false)
public class DiaryAnalysisSummary extends BaseRDBEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", nullable = false, foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (diary_id) REFERENCES diaries(id) ON DELETE CASCADE"))
    private Diary diary;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private AnalysisStatus status = AnalysisStatus.PROCESSING;

    public void updateStatus(AnalysisStatus status) {
        this.status = status;
    }

    public void updateAnalysisData(Map<String, Double> big5Scores, String domainClassification,
                                   String finalConclusion, List<String> keywords) {
        this.big5Scores = big5Scores;
        this.domainClassification = domainClassification;
        this.finalConclusion = finalConclusion;
        this.keywords = keywords;
    }
}