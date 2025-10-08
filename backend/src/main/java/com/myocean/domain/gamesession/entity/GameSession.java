package com.myocean.domain.gamesession.entity;

import com.myocean.domain.gamesession.enums.GameType;
import com.myocean.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.AccessLevel;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "game_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user"})
@EqualsAndHashCode(of = "id")
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"))
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_type", nullable = false)
    private GameType gameType;

    @CreationTimestamp
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Setter(AccessLevel.PRIVATE)  // Setter를 private으로 제한
    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    public void finish() {
        if (this.finishedAt != null) { throw new IllegalStateException("게임 세션은 이미 종료"); }
        this.finishedAt = LocalDateTime.now();
    }
}