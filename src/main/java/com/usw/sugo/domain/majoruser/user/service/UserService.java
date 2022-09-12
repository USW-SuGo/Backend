package com.usw.sugo.domain.majoruser.user.service;

import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.user.repository.UserRepository;
import com.usw.sugo.global.status.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User softSaveUser(String email) {
        User user = User.builder()
                .email(email)
                .status(String.valueOf(Status.NOT_AUTH))
                .build();

        userRepository.save(user);

        return user;
    }
}
