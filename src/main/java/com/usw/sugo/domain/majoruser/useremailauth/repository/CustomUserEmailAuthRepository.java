package com.usw.sugo.domain.majoruser.useremailauth.repository;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface CustomUserEmailAuthRepository {

    void confirmToken(String payload);

}
