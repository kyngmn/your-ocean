package com.myocean.domain.ug.entity;

import com.myocean.domain.ug.enums.MoneySize;
import com.myocean.domain.ug.enums.PersonaType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "game_ug_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class GameUgOrder {

    @Id
    private Long id;

    @Column(name = "role_type", nullable = false)
    private Integer roleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "persona_type", nullable = false)
    private PersonaType personaType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MoneySize money;

    @Column(nullable = false)
    private Integer rate;
}