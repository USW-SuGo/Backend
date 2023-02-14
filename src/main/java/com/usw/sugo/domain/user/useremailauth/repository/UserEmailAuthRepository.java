package com.usw.sugo.domain.user.useremailauth.repository;

import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.useremailauth.UserEmailAuth;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface UserEmailAuthRepository extends JpaRepository<UserEmailAuth, Long>,
    CustomUserEmailAuthRepository {

    Optional<UserEmailAuth> findByPayload(String payload);

    Optional<UserEmailAuth> findByUser(User user);

    void deleteByUser(User user);
}
