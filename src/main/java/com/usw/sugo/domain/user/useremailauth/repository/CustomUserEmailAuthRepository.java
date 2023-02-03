package com.usw.sugo.domain.user.useremailauth.repository;

import com.usw.sugo.domain.user.useremailauth.UserEmailAuth;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomUserEmailAuthRepository {

    void deleteByUserId(Long userId);

    List<UserEmailAuth> loadNotAuthenticatedUserEmailAuth();

}
