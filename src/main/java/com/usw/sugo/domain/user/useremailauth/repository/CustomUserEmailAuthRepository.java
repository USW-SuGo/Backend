package com.usw.sugo.domain.user.useremailauth.repository;

import com.usw.sugo.domain.user.useremailauth.UserEmailAuth;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomUserEmailAuthRepository {

    void deleteByUserId(Long userId);

    List<UserEmailAuth> loadNotAuthenticatedUserEmailAuth();

}
