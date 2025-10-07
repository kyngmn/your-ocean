package com.myocean.domain.survey.entity;

import com.myocean.domain.big5.entity.Big5Code;
import com.myocean.global.common.BaseRDBEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "surveys")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id", callSuper = false)
public class Survey extends BaseRDBEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "big_five_id", nullable = false)
    private Big5Code big5Code;

    @Column(name = "is_reverse_scored", nullable = false)
    private Boolean isReverseScored;

    @Column(name = "question_text", nullable = false, length = 500)
    private String questionText;
}