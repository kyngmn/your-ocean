package com.myocean.domain.user.entity;

import com.myocean.domain.common.BaseRDBEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "user_personas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "actors"})
@EqualsAndHashCode(of = "id", callSuper = false)
public class UserPersona extends BaseRDBEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "user_o", nullable = false)
    private Integer userO;

    @Column(name = "user_c", nullable = false)
    private Integer userC;

    @Column(name = "user_e", nullable = false)
    private Integer userE;

    @Column(name = "user_a", nullable = false)
    private Integer userA;

    @Column(name = "user_n", nullable = false)
    private Integer userN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Actor> actors;
}