package com.usw.sugo.domain.user.user;

import com.usw.sugo.global.util.basetime.BaseTimeEntity;
import com.usw.sugo.global.util.factory.BCryptPasswordFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    public void addCountTradeAttempt() {
        this.countTradeAttempt += 1;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void encryptPassword(String password) {
        this.password = BCryptPasswordFactory.getBCryptPasswordEncoder().encode(password);
    }

    public void modifyingStatusToAvailable() {
        this.status = "AVAILABLE";
    }
}
