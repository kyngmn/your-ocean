package com.myocean.domain.ug.entity;

import com.myocean.domain.gamemanagement.entity.GameSession;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "game_ug_responses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"gameSession", "gameUgOrder"})
@EqualsAndHashCode(of = "id")
public class GameUgResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(nullable = false)
    private Integer round;

    @Column(name = "order_id")
    private Long orderId;

    @Column(nullable = false)
    private Integer money;

    @Column(name = "is_accepted", nullable = false)
    @Builder.Default
    private Boolean isAccepted = true;

    @Column(name = "proposal_rate")
    private Integer proposalRate;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", insertable = false, updatable = false)
    private GameSession gameSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private GameUgOrder gameUgOrder;
}