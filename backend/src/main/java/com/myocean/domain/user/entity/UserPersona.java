package com.myocean.domain.user.entity;

import com.myocean.domain.user.listener.PersonaActorCreator;
import com.myocean.global.common.BaseRDBEntity;
import com.myocean.global.enums.BigCode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_personas",
       uniqueConstraints = @UniqueConstraint(
           name = "uq_user_persona",
           columnNames = {"user_id", "big_code"}
       ))
@EntityListeners(PersonaActorCreator.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "user")
@EqualsAndHashCode(of = "id", callSuper = false)
public class UserPersona extends BaseRDBEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"))
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "big_code", nullable = false)
    private BigCode bigCode;

    @Column(nullable = false)
    private Short score;

    @PrePersist
    @PreUpdate
    private void validateScore() {
        if (score == null || score < 0 || score > 100) {
            throw new IllegalArgumentException("big5 값은 0부터 100사이어야합니다");
        }
    }
}