package com.myocean.domain.mychat.entity;

import com.myocean.global.common.BaseRDBEntity;
import com.myocean.global.enums.AnalysisStatus;
import com.myocean.domain.user.entity.Actor;
import com.myocean.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "my_chat_messages",
        indexes = {
                @Index(name = "idx_user_created", columnList = "user_id, created_at"),
                @Index(name = "idx_user_id", columnList = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "senderActor"})
@EqualsAndHashCode(of = "id",  callSuper = true)
public class MyChatMessage extends BaseRDBEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_actor_id", nullable = false, foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (sender_actor_id) REFERENCES actors(id) ON DELETE CASCADE"))
    private Actor senderActor;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "analysis_status", length = 20)
    private AnalysisStatus analysisStatus;

    public void updateAnalysisStatus(AnalysisStatus status) {
        this.analysisStatus = status;
    }
}