package com.myocean.domain.diary.entity;

import com.myocean.domain.common.BaseRDBEntity;
import com.myocean.domain.user.entity.Actor;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "diary_analysis_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"diary", "senderActor"})
@EqualsAndHashCode(of = "id")
public class DiaryAnalysisMessage extends BaseRDBEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "diary_id", nullable = false)
    private Integer diaryId;

    @Column(name = "sender_actor_id", nullable = false)
    private Integer senderActorId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "message_order")
    private Integer messageOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", insertable = false, updatable = false)
    private Diary diary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_actor_id", insertable = false, updatable = false)
    private Actor senderActor;
}