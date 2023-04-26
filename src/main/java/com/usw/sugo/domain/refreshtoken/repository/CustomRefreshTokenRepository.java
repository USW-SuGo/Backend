package com.usw.sugo.domain.refreshtoken.repository;

import com.usw.sugo.domain.user.user.User;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomRefreshTokenRepository {

    void refreshPayload(Long userId, String payload);

    void deleteByUser(User user);
}
