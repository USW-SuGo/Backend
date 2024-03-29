package com.usw.sugo.domain.user.user;

import com.usw.sugo.global.util.basetime.BaseTimeEntity;
import com.usw.sugo.global.util.factory.BCryptPasswordFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseTimeEntity implements UserDetails {

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
    private BigDecimal mannerScore;

    @Column
    private Long countMannerEvaluation;

    @Column
    private Long countTradeAttempt;

    @Column
    private LocalDateTime recentUpPost;

    @Column
    private LocalDateTime recentEvaluationManner;

    @Column
    private String status;

    @Column
    private Boolean pushAlarmStatus;

    @Column
    private String fcmToken;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return loginId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void addCountTradeAttempt() {
        this.countTradeAttempt += 1;
    }

    public void encryptPassword(String password) {
        this.password = BCryptPasswordFactory.getBCryptPasswordEncoder().encode(password);
    }

    public void modifyingStatusToAvailable() {
        this.status = "AVAILABLE";
    }

    public void updateMannerGrade(BigDecimal grade) {
        this.mannerScore = this.mannerScore.add(grade);
        this.countMannerEvaluation += 1;
        this.mannerGrade = mannerScore.divide(
                BigDecimal.valueOf(this.countMannerEvaluation), RoundingMode.FLOOR
            );
    }

    public void updateRecentUpPost() {
        this.recentUpPost = LocalDateTime.now();
    }

    public void updateRecentEvaluationManner() {
        this.recentEvaluationManner = LocalDateTime.now();
    }

    public void updatePushAlarmStatus(Boolean pushAlarmStatus) {
        this.pushAlarmStatus = pushAlarmStatus;
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
