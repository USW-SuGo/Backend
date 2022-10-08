package com.usw.sugo.domain.majoruser;

import com.usw.sugo.domain.status.Status;
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
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Enumerated(EnumType.STRING)
    @Column
    private Status status;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;
}
