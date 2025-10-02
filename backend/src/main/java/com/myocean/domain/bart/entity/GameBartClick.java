package com.myocean.domain.bart.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "game_bart_clicks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "gameBartResponse")
@EqualsAndHashCode(of = "id")
public class GameBartClick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "response_id", nullable = false)
    private Long responseId;

    @Column(name = "click_index", nullable = false)
    private Integer clickIndex;

    @Column(name = "clicked_at", nullable = false)
    private LocalDateTime clickedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "response_id", insertable = false, updatable = false)
    private GameBartResponse gameBartResponse;
}