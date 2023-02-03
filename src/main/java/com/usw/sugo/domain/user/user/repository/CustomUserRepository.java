package com.usw.sugo.domain.user.user.repository;

import com.usw.sugo.domain.user.user.User;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomUserRepository {

    String findToAvailableNicknameNumber(String department);

    void setRecentUpPostingDate(Long id);

    void deleteByUser(User user);
}
