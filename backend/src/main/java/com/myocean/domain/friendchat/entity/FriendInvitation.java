package com.myocean.domain.friendchat.entity;

import com.myocean.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "friend_invitations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"inviterUser", "inviteeUser"})
@EqualsAndHashCode(of = "id")
public class FriendInvitation {

    @PrePersist
    public void generateToken() {
        if (this.invitationToken == null) {
            this.invitationToken = UUID.randomUUID().toString();
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "inviter_user_id", nullable = false)
    private Integer inviterUserId;

    @Column(name = "invitee_user_id")
    private Integer inviteeUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 9)
    private InvitationStatus status;

    @Column(name = "invitation_token", unique = true, nullable = false)
    private String invitationToken;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_user_id", insertable = false, updatable = false)
    private User inviterUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitee_user_id", insertable = false, updatable = false)
    private User inviteeUser;

    public enum InvitationStatus {
        PENDING, ACCEPTED, REJECTED
    }
}