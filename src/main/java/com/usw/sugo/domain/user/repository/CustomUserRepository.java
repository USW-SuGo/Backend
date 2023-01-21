package com.usw.sugo.domain.user.repository;

import com.usw.sugo.domain.user.dto.UserResponseDto;
import com.usw.sugo.domain.user.User;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface CustomUserRepository {

    UserResponseDto.UserPageResponseForm loadUserPage(User user);

    void modifyingStatusToAvailable(Long id);

    void passwordEncode(User user, Long userId);

    void editPassword(Long id, String password);

    void setModifiedDate(Long id);

    void setRecentMannerGradeDate(BigDecimal grade, long targetUserId, long evaluatingUserId);

    void setRecentUpPostingDate(Long id);

    void editNickname(long id, String nickname);

    void plusCountTradeAttempt(long sellerId, long buyerId);

    void deleteUserNotEmailAuth(long userId);

}
