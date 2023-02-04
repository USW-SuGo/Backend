package com.usw.sugo.domain.user.user.controller;

import com.usw.sugo.domain.user.user.repository.UserRepository;
import com.usw.sugo.domain.user.user.service.UserServiceUtility;
import com.usw.sugo.domain.user.useremailauth.UserEmailAuth;
import com.usw.sugo.domain.user.useremailauth.repository.UserEmailAuthRepository;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.usw.sugo.global.exception.ExceptionType.*;

@Component
@RequiredArgsConstructor
public class UserControllerValidator {
    private final UserRepository userRepository;
    private final UserEmailAuthRepository userEmailAuthRepository;
    private final UserServiceUtility userServiceUtility;

    public User validateUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionType.USER_NOT_EXIST));
    }

    public User validateUserByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ExceptionType.USER_NOT_EXIST));
    }

    public User validateUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ExceptionType.USER_NOT_EXIST));
    }

    public UserEmailAuth validateUserEmailAuth(Long userId) {
        return userEmailAuthRepository.findByUser(validateUserById(userId))
                .orElseThrow(() -> new CustomException(USER_NOT_SEND_AUTH_EMAIL));
    }

    public void validatePasswordForEditPassword(Long userId, String password) {
        if (userServiceUtility.matchingPassword(userId, password)) {
            throw new CustomException(IS_SAME_PASSWORD);
        }
    }

    public void validatePasswordForAuthorization(Long userId, String password) {
        if (userServiceUtility.matchingPassword(userId, password)) {
            throw new CustomException(USER_NOT_EXIST);
        }
    }
}
