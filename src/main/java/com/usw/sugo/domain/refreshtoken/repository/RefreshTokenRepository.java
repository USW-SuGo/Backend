package com.usw.sugo.domain.refreshtoken.repository;

import com.usw.sugo.domain.refreshtoken.RefreshToken;
import com.usw.sugo.domain.user.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>,
    CustomRefreshTokenRepository {

    Optional<RefreshToken> findByUserId(Long id);

    Optional<RefreshToken> findByPayload(String payload);

    void deleteByUser(User user);
}
