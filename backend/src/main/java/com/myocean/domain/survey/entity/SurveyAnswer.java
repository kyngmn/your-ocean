package com.myocean.domain.survey.entity;

import com.myocean.global.common.BaseRDBEntity;
import com.myocean.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "survey_answers",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "survey_answers_uniq",
            columnNames = {"user_id", "survey_id"}
        )
    }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"survey", "user"})
@EqualsAndHashCode(of = "id", callSuper = false)
public class SurveyAnswer extends BaseRDBEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Short value;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

}