package com.myocean.domain.survey.entity;

import com.myocean.global.common.BaseRDBEntity;
import com.myocean.global.enums.BigCode;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "big_five_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "surveys")
@EqualsAndHashCode(of = "id", callSuper = false)
public class BigFiveCode extends BaseRDBEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Enumerated(EnumType.STRING)
    @Column(name = "big_code", nullable = false)
    private BigCode bigCode;

    @Column(name = "small_code", nullable = false, length = 25)
    private String smallCode;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "bigFiveCode", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Survey> surveys;
}