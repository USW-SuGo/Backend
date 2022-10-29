package com.usw.sugo.domain.majoruser.emailauth.repository;

import com.usw.sugo.domain.majoruser.UserEmailAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserEmailAuthRepository extends JpaRepository<UserEmailAuth, Long>, CustomUserEmailAuthRepository {
    Optional<UserEmailAuth> findByPayload(String payload);
    Optional<UserEmailAuth> findByUserId(long userId);
}
