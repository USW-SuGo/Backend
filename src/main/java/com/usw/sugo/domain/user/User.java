package com.usw.sugo.domain.user;

import com.usw.sugo.global.util.basetime.BaseTimeEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String loginId;

    @Column
    private String nickname;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private BigDecimal mannerGrade;

    @Column
    private long countMannerEvaluation;

    @Column
    private long countTradeAttempt;

    @Column
    private LocalDateTime recentUpPost;

    @Column
    private LocalDateTime recentEvaluationManner;

    @Column
    private String status;
}
