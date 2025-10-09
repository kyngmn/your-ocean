package com.myocean.domain.friendchat.entity;

import com.myocean.domain.user.entity.Actor;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "friend_chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"room", "senderActor"})
@EqualsAndHashCode(of = "id")
public class FriendChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "room_id", nullable = false)
    private Integer roomId;

    @Column(name = "sender_actor_id", nullable = false)
    private Long senderActorId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", insertable = false, updatable = false)
    private Friend room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_actor_id", insertable = false, updatable = false)
    private Actor senderActor;
}