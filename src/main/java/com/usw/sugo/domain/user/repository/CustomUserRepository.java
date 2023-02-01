package com.usw.sugo.domain.user.repository;

import com.usw.sugo.domain.user.User;
import com.usw.sugo.domain.user.dto.UserResponseDto.UserPageResponseForm;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface CustomUserRepository {

    UserPageResponseForm loadUserPage(User user);

    void setRecentMannerGradeDate(BigDecimal grade, long targetUserId, long evaluatingUserId);

    void setRecentUpPostingDate(Long id);

    void plusCountTradeAttempt(long sellerId, long buyerId);
}
