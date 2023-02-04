package com.usw.sugo.domain.user.user;

import com.usw.sugo.global.util.basetime.BaseTimeEntity;
import com.usw.sugo.global.util.factory.BCryptPasswordFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collection;

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
    private long countMannerEvaluation;

    @Column
    private long countTradeAttempt;

    @Column
    private LocalDateTime recentUpPost;

    @Column
    private LocalDateTime recentEvaluationManner;

    @Column
    private String status;

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

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void encryptPassword(String password) {
        this.password = BCryptPasswordFactory.getBCryptPasswordEncoder().encode(password);
    }

    public void modifyingStatusToAvailable() {
        this.status = "AVAILABLE";
    }

    public void updateMannerGrade(BigDecimal grade) {
        this.countMannerEvaluation += 1;
        this.mannerGrade = mannerGrade.divide(BigDecimal.valueOf(this.countMannerEvaluation), RoundingMode.FLOOR);
    }

    public void updateRecentEvaluationManner() {
        this.recentEvaluationManner = LocalDateTime.now();
    }
}
