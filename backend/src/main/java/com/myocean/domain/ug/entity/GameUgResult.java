package com.myocean.domain.ug.entity;

import com.myocean.domain.gamesession.entity.GameSession;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "game_ug_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "gameSession")
@EqualsAndHashCode(of = "sessionId")
public class GameUgResult {

    @Id
    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "earned_amount", nullable = false)
    private Integer earnedAmount;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", insertable = false, updatable = false)
    private GameSession gameSession;
}