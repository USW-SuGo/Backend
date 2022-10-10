package com.usw.sugo.domain.majoruser.user.repository;

import com.usw.sugo.domain.majoruser.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDetailsRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByLoginId(String loginId);
}
