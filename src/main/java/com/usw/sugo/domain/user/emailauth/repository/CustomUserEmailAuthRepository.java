package com.usw.sugo.domain.user.emailauth.repository;

import com.usw.sugo.domain.user.entity.UserEmailAuth;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomUserEmailAuthRepository {

    void confirmToken(String payload);

    void deleteNotProceedEmailAuthBeforeTenMinutes(List<UserEmailAuth> notAuthenticatedUserEmailAuth);

    List<UserEmailAuth> loadNotAuthenticatedUserEmailAuth();

}
