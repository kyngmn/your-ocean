package com.myocean.domain.diary.entity;

import com.myocean.domain.user.entity.User;
import com.myocean.global.common.BaseRDBEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "diaries",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_diary_date",
                        columnNames = {"user_id", "diary_date"}
                )
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "user")
@EqualsAndHashCode(of = "id", callSuper = false)
public class Diary extends BaseRDBEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"))
    private User user;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "diary_date", nullable = false)
    private LocalDate diaryDate;
}
