package com.usw.sugo.domain.user.user.repository;

import com.usw.sugo.domain.user.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {

    Optional<User> findByEmail(String email);

    Optional<User> findByLoginId(String loginId);
}
