package com.usw.sugo.domain.user.user.service;

import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Test
    void executeEditPassword() {
        User user = userRepository.findById(32L).get();
        System.out.println(userService.executeEditPassword(user, "qwer1234!"));
    }
}