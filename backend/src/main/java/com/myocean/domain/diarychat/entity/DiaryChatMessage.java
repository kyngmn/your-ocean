package com.myocean.domain.diarychat.entity;

import com.myocean.domain.diary.entity.Diary;
import com.myocean.domain.user.entity.Actor;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "diary_chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"diary", "senderActor"})
@EqualsAndHashCode(of = "id")
public class DiaryChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "diary_id", nullable = false)
    private Integer diaryId;

    @Column(name = "sender_actor_id", nullable = false)
    private Integer senderActorId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", insertable = false, updatable = false)
    private Diary diary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_actor_id", insertable = false, updatable = false)
    private Actor senderActor;
}