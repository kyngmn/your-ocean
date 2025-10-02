package com.myocean.domain.bart.entity;

import com.myocean.domain.gamemanagement.entity.GameSession;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_bart_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "gameSession")
@EqualsAndHashCode(of = "sessionId")
public class GameBartResult {

    @Id
    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "reward_amount", nullable = false)
    private Integer rewardAmount;

    @Column(name = "missed_reward", nullable = false)
    private Integer missedReward;

    @Column(name = "total_balloons", nullable = false)
    private Integer totalBalloons;

    @Column(name = "success_balloons", nullable = false)
    private Integer successBalloons;

    @Column(name = "fail_balloons", nullable = false)
    private Integer failBalloons;

    @Column(name = "avg_pumps", nullable = false, precision = 6, scale = 2)
    private BigDecimal avgPumps;

    @CreationTimestamp
    @Column(name = "computed_at", nullable = false)
    private LocalDateTime computedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    @MapsId
    private GameSession gameSession;
}