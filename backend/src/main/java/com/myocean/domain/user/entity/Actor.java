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
@EqualsAndHashCode(of = "id")
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
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", insertable = false, updatable = false)
    private UserPersona persona;
}