package com.myocean.domain.user.entity;

import com.myocean.domain.user.enums.ActorKind;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "actors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "persona"})
@EqualsAndHashCode(of = "id", callSuper = false)
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActorKind kind;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "persona_id")
    private Integer personaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false, foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", insertable = false, updatable = false, foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (persona_id) REFERENCES user_personas(id) ON DELETE CASCADE"))
    private UserPersona persona;

    @PrePersist
    @PreUpdate
    private void validate() {
        if (kind == ActorKind.USER) {
            if (userId == null) {
                throw new IllegalStateException("USER 타입 Actor는 userId가 필수");
            }
            if (personaId != null) {
                throw new IllegalStateException("USER 타입 Actor는 personaId를 가질 수 없음");
            }
        } else if (kind == ActorKind.PERSONA) {
            if (personaId == null) {
                throw new IllegalStateException("PERSONA 타입 Actor는 personaId가 필수");
            }
            if (userId != null) {
                throw new IllegalStateException("PERSONA 타입 Actor는 userId를 가질 수 없음");
            }
        }
    }
}