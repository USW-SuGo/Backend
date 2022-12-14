package com.usw.sugo.domain.user.emailauth.repository;

import com.usw.sugo.domain.user.entity.User;
import com.usw.sugo.domain.user.entity.UserEmailAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public interface UserEmailAuthRepository extends JpaRepository<UserEmailAuth, Long>, CustomUserEmailAuthRepository {
    Optional<UserEmailAuth> findByPayload(String payload);
    Optional<UserEmailAuth> findByUserId(long userId);
    void deleteByUser(User user);
}
