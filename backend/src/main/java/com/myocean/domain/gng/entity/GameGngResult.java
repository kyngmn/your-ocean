package com.myocean.domain.gng.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_gng_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "sessionId")
public class GameGngResult {

    @Id
    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "total_correct_cnt", nullable = false)
    private Integer totalCorrectCnt;

    @Column(name = "total_incorrect_cnt", nullable = false)
    private Integer totalIncorrectCnt;

    @Column(name = "nogo_incorrect_cnt", nullable = false)
    private Integer nogoIncorrectCnt;

    @Column(name = "avg_reaction_time", nullable = false, precision = 6, scale = 2)
    private BigDecimal avgReactionTime;

    @Column(name = "played_at", nullable = false)
    private LocalDateTime playedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;
}