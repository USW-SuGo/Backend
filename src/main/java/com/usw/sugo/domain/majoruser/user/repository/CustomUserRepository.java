package com.usw.sugo.domain.majoruser.user.repository;

import com.usw.sugo.domain.majoruser.User;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface CustomUserRepository {

    void modifyingStatusToAvailable(Long id);

    void passwordEncode(User user, Long userId);

    void editPassword(Long id, String password);

    void setModifiedDate(Long id);

    void setRecentMannerGradeDate(BigDecimal grade, long targetUserId, long evaluatingUserId);

    void setRecentUpPostingDate(Long id);

}
