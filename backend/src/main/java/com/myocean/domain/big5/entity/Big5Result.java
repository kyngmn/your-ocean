package com.myocean.domain.big5.entity;

import com.myocean.domain.big5.enums.Big5SourceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "big5_results",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_big5_user_source",
                columnNames = {"user_id", "source_type", "source_id"}
        ))
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Big5Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private Big5SourceType sourceType;

    @Column(name = "source_id", nullable = false)
    private Long sourceId;

    @Column(name = "result_o")
    private Integer resultO;

    @Column(name = "result_c")
    private Integer resultC;

    @Column(name = "result_e")
    private Integer resultE;

    @Column(name = "result_a")
    private Integer resultA;

    @Column(name = "result_n")
    private Integer resultN;

    @Column(name = "computed_at", nullable = false)
    private LocalDateTime computedAt;

    @PrePersist
    protected void onCreate() {
        computedAt = LocalDateTime.now();
    }
}