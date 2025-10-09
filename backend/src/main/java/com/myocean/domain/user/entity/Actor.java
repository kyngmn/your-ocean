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
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActorKind kind;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (persona_id) REFERENCES user_personas(id) ON DELETE CASCADE"))
    private UserPersona persona;

    @PrePersist
    @PreUpdate
    private void validate() {
        if (kind == ActorKind.USER) {
            if (user == null) {
                throw new IllegalStateException("USER 타입 Actor는 user가 필수");
            }
            if (persona != null) {
                throw new IllegalStateException("USER 타입 Actor는 persona를 가질 수 없음");
            }
        } else if (kind == ActorKind.PERSONA) {
            if (persona == null) {
                throw new IllegalStateException("PERSONA 타입 Actor는 persona가 필수");
            }
            if (user != null) {
                throw new IllegalStateException("PERSONA 타입 Actor는 user를 가질 수 없음");
            }
        } else if (kind == ActorKind.SYSTEM) {
            // SYSTEM 타입은 user, persona 둘 다 NULL 가능 (기본 OCEAN Actor)
            if (user != null || persona != null) {
                throw new IllegalStateException("SYSTEM 타입 Actor는 user, persona를 가질 수 없음");
            }
        }
    }
}