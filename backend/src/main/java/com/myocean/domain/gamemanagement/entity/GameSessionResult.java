package com.myocean.domain.gamemanagement.entity;

import com.myocean.domain.gamemanagement.enums.SessionType;
import com.myocean.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "game_session_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"gameSession", "user"})
@EqualsAndHashCode(of = "sessionId")
public class GameSessionResult {

    @Id
    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false)
    private SessionType sessionType;

    @Column(name = "result_o", nullable = false)
    private Integer resultO;

    @Column(name = "result_c", nullable = false)
    private Integer resultC;

    @Column(name = "result_e", nullable = false)
    private Integer resultE;

    @Column(name = "result_a", nullable = false)
    private Integer resultA;

    @Column(name = "result_n", nullable = false)
    private Integer resultN;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    @MapsId
    private GameSession gameSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}