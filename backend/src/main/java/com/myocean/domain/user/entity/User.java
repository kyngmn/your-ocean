package com.myocean.domain.user.entity;

import com.myocean.global.common.BaseRDBEntity;
import com.myocean.domain.user.enums.AiStatus;
import com.myocean.domain.user.enums.Provider;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_provider_social_id", columnNames = {"provider", "social_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"userPersonas", "actors"})
@EqualsAndHashCode(of = "id", callSuper = false)
public class User extends BaseRDBEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Column(name = "social_id", nullable = false, length = 255)
    private String socialId;

    @Column(nullable = false, length = 10)
    private String nickname;

    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "ai_status", nullable = false)
    @Builder.Default
    private AiStatus aiStatus = AiStatus.UNSET;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<UserPersona> userPersonas = new ArrayList<>(); // NPE 위험 대비 초기화

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Actor> actors = new ArrayList<>(); // NPE 위험 대비 초기화
}