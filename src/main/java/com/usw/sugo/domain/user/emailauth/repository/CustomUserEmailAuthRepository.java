package com.usw.sugo.domain.user.emailauth.repository;

import com.usw.sugo.domain.user.UserEmailAuth;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomUserEmailAuthRepository {

    void confirmToken(String payload);

    void deleteBeforeWeek(List<UserEmailAuth> notAuthenticatedUserEmailAuth);

    List<UserEmailAuth> loadNotAuthenticatedUserEmailAuth();

}
