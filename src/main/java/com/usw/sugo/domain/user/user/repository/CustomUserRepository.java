package com.usw.sugo.domain.user.user.repository;

import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.dto.UserResponseDto.UserPageResponseForm;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface CustomUserRepository {

    String findToAvailableNicknameNumber(String department);
    UserPageResponseForm loadUserPage(User user);
    void setRecentMannerGradeDate(BigDecimal grade, long targetUserId, long evaluatingUserId);
    void setRecentUpPostingDate(Long id);
    void plusCountTradeAttempt(long sellerId, long buyerId);

    void deleteByUser(User user);
}
