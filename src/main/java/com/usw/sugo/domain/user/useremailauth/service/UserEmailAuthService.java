package com.usw.sugo.domain.user.useremailauth.service;

import static com.usw.sugo.global.exception.ExceptionType.USER_NOT_EXIST;

import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.service.UserServiceUtility;
import com.usw.sugo.domain.user.useremailauth.UserEmailAuth;
import com.usw.sugo.domain.user.useremailauth.repository.UserEmailAuthRepository;
import com.usw.sugo.global.exception.CustomException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserEmailAuthService {

    private final UserServiceUtility userServiceUtility;
    private final UserEmailAuthRepository userEmailAuthRepository;
    private final List<Character> charNumberSet = UserServiceUtility.charNumberSet;
    private final int numberLen = charNumberSet.size();


    public UserEmailAuth loadUserEmailAuthByUser(User user) {
        if (userEmailAuthRepository.findByUser(user).isPresent()) {
            return userEmailAuthRepository.findByUser(user).get();
        }
        throw new CustomException(USER_NOT_EXIST);
    }

    public String saveUserEmailAuth(User user) {
        final UserEmailAuth userEmailAuth = UserEmailAuth.builder()
            .payload(createEmailAuthPayload(user.getId()))
            .createdAt(LocalDateTime.now())
            .user(user)
            .status(false)
            .build();
        userEmailAuthRepository.save(userEmailAuth);
        return userEmailAuth.getPayload();
    }

    private String createEmailAuthPayload(Long userId) {
        User user = userServiceUtility.loadUserById(userId);
        final StringBuilder payload = new StringBuilder();
        final SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(new Date().getTime());

        int index = 0;
        // 인증번호는 8자리로 이루어져있다.
        for (int i = 0; i < 8; i++) {
            index = secureRandom.nextInt(numberLen);
            payload.append(charNumberSet.get(index));
        }
        return payload.toString();
    }

    @Transactional
    public void deleteConfirmedEmailAuthByUser(User user) {
        userEmailAuthRepository.deleteByUser(user);
    }

    private List<UserEmailAuth> getNotAuthenticatedUserEmailAuth() {
        return userEmailAuthRepository.loadNotAuthenticatedUserEmailAuth();
    }

    @Transactional
    // 1시간 내로 인증을 수행하지 않으면 이메일 테이블 제거 -> 유저 테이블 제거
    // 리팩터링 필요
    // @Scheduled(cron = "0 * * * * *")
    public void deleteNotAuthenticatedUserAndToken() {
        final List<UserEmailAuth> loadedNotAuthenticatedUser = getNotAuthenticatedUserEmailAuth();
        for (UserEmailAuth userEmailAuth : loadedNotAuthenticatedUser) {
            userEmailAuthRepository.deleteByUserId(userEmailAuth.getUser().getId());
            userServiceUtility.deleteUser(userEmailAuth.getUser());
        }
    }
}
