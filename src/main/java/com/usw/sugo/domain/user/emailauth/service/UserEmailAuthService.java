package com.usw.sugo.domain.user.emailauth.service;

import com.usw.sugo.domain.user.User;
import com.usw.sugo.domain.user.UserEmailAuth;
import com.usw.sugo.domain.user.user.repository.UserRepository;
import com.usw.sugo.domain.user.emailauth.repository.UserEmailAuthRepository;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserEmailAuthService {

    private final UserRepository userRepository;
    private final UserEmailAuthRepository userEmailAuthRepository;

    // 인증번호 전송을 위한 랜덤 인증번호 생성 및 DB에 저장
    public String createEmailAuthPayload(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST));

        StringBuilder payload = new StringBuilder();

        SecureRandom sr = new SecureRandom();
        sr.setSeed(new Date().getTime());


        char[] charNumberSet = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        int idx = 0;
        int numberLen = charNumberSet.length;

        // 인증번호는 8자리로 이루어져있다.
        for (int i = 0; i < 8; i++) {
            idx = sr.nextInt(numberLen);
            payload.append(charNumberSet[idx]);
        }

        UserEmailAuth userEmailAuth = UserEmailAuth.builder()
                .payload(payload.toString())
                .createdAt(LocalDateTime.now())
                .user(user)
                .status(false)
                .build();

        userEmailAuthRepository.save(userEmailAuth);

        return payload.toString();
    }

    // 인증번호 인증 됨에 따른 DB에 반영
    public void authorizeEmailByPayload(String payload) {
        userEmailAuthRepository.confirmToken(payload);
    }

    // 인증하지 않은 유저 목록 가져오기
    public List<UserEmailAuth> getNotAuthenticatedUserEmailAuth() {
        return userEmailAuthRepository.loadNotAuthenticatedUserEmailAuth();
    }

    // 1시간 내로 인증을 수행하지 않으면 제거
    @Scheduled(cron = "0 * * * * *")
    public void deleteNotAuthenticatedUserAndToken() {
        List<UserEmailAuth> loadedNotAuthenticatedUser = getNotAuthenticatedUserEmailAuth();
        userEmailAuthRepository.deleteBeforeWeek(loadedNotAuthenticatedUser);
    }
}
