package com.usw.sugo.domain.majoruser.emailauth.repository;

import com.usw.sugo.domain.majoruser.UserEmailAuth;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomUserEmailAuthRepository {

    void confirmToken(String payload);

    void deleteBeforeWeek(List<UserEmailAuth> notAuthenticatedUserEmailAuth);

    List<UserEmailAuth> loadNotAuthenticatedUserEmailAuth();

}
