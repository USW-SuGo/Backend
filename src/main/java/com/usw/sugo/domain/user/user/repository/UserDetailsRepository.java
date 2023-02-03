package com.usw.sugo.domain.user.user.repository;

import com.usw.sugo.domain.user.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDetailsRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);
}
