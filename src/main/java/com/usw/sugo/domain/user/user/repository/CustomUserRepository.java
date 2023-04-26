package com.usw.sugo.domain.user.user.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface CustomUserRepository {

    String findToAvailableNicknameNumber(String department);
}
