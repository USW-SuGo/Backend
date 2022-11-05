package com.usw.sugo.domain.majoruser.emailauth.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface CustomUserEmailAuthRepository {

    void confirmToken(String payload);

    void deleteBeforeWeek();

}
