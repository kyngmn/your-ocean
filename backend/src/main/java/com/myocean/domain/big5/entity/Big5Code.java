package com.myocean.domain.big5.entity;

import com.myocean.global.common.BaseRDBEntity;
import com.myocean.global.enums.BigCode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "big_five_codes")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id", callSuper = false)
public class Big5Code extends BaseRDBEntity {

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
}