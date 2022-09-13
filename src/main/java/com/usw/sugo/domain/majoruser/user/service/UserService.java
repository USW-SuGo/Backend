package com.usw.sugo.domain.majoruser.user.service;

import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.user.repository.UserRepository;
import com.usw.sugo.global.status.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public User softSaveUser(String email) {
        User user = User.builder()
                .email(email)
                .status(String.valueOf(Status.NOT_AUTH))
                .build();

        userRepository.save(user);

        return user;
    }

    @Transactional
    public boolean matchingPassword(String inputPassword, User user) {
        return bCryptPasswordEncoder.matches(inputPassword, user.getPassword());
    }

    @Transactional
    public boolean isSamePassword(Long id, String editRequestPassword) {
        if (bCryptPasswordEncoder.matches(editRequestPassword, userRepository.findById(id).get().getPassword())) {
            return true;
        }
        return false;
    }
}
