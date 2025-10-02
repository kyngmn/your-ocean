package com.myocean.domain.gng.entity;

import com.myocean.domain.gng.enums.GngStimulus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "game_gng_responses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class GameGngResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "round", nullable = false)
    private Short round;

    @Enumerated(EnumType.STRING)
    @Column(name = "stimulus_type", nullable = false)
    private GngStimulus stimulusType;

    @Column(name = "stimulus_started_at", nullable = false)
    private LocalDateTime stimulusStartedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "is_succeeded")
    private Boolean isSucceeded;
}