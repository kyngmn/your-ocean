package com.myocean.domain.survey.entity;

import com.myocean.global.common.BaseRDBEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "surveys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "surveyResponses")
@EqualsAndHashCode(of = "id", callSuper = false)
public class Survey extends BaseRDBEntity {

    @Id
    private Short id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "big_five_id", nullable = false)
    private BigFiveCode bigFiveCode;

    @Column(name = "is_reverse_scored", nullable = false)
    private Boolean isReverseScored;

    @Column(name = "question_text", nullable = false, length = 500)
    private String questionText;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SurveyResponse> surveyResponses;
}