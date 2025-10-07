package com.myocean.domain.bart.entity;

import com.myocean.domain.bart.enums.BalloonColor;
import com.myocean.domain.gamesession.entity.GameSession;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "game_bart_responses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"gameSession", "gameBartClicks"})
@EqualsAndHashCode(of = "id")
public class GameBartResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "round_index", nullable = false)
    private Integer roundIndex;

    @Enumerated(EnumType.STRING)
    @Column(name = "color", nullable = false)
    private BalloonColor color;

    @Column(name = "popping_point", nullable = false)
    private Integer poppingPoint;

    @Column(name = "is_popped", nullable = false)
    private Boolean isPopped;

    @Column(name = "pumping_cnt")
    private Integer pumpingCnt;

    @CreationTimestamp
    @Column(name = "played_at", nullable = false)
    private LocalDateTime playedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", insertable = false, updatable = false)
    private GameSession gameSession;

    @OneToMany(mappedBy = "gameBartResponse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GameBartClick> gameBartClicks;
}