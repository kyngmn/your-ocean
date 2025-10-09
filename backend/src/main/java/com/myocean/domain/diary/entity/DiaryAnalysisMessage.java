package com.myocean.domain.diary.entity;

import com.myocean.global.common.BaseRDBEntity;
import com.myocean.domain.user.entity.Actor;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "diary_analysis_messages",
        indexes = {
                @Index(name = "idx_diary_created", columnList = "diary_id, created_at"),
                @Index(name = "idx_diary_id", columnList = "diary_id")
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"diary", "senderActor"})
@EqualsAndHashCode(of = "id", callSuper = false)
public class DiaryAnalysisMessage extends BaseRDBEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", nullable = false, foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (diary_id) REFERENCES diaries(id) ON DELETE CASCADE"))
    private Diary diary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_actor_id", nullable = false, foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (sender_actor_id) REFERENCES actors(id) ON DELETE CASCADE"))
    private Actor senderActor;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "message_order")
    private Integer messageOrder;
}