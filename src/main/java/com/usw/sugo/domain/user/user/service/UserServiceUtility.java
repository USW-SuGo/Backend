package com.usw.sugo.domain.user.user.service;

import static com.usw.sugo.global.exception.ExceptionType.EMAIL_NOT_VALIDATED;
import static com.usw.sugo.global.exception.ExceptionType.USER_NOT_EXIST;

import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.repository.UserRepository;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.util.nickname.NicknameGenerator;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceUtility {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final NicknameGenerator nicknameGenerator;

    public User loadUserById(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            return userRepository.findById(userId).get();
        }
        throw new CustomException(USER_NOT_EXIST);
    }

    public User loadUserByLoginId(String loginId) {
        if (userRepository.findByLoginId(loginId).isPresent()) {
            return userRepository.findByLoginId(loginId).get();
        }
        throw new CustomException(USER_NOT_EXIST);
    }

    public User loadUserByEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            return userRepository.findByEmail(email).get();
        }
        throw new CustomException(USER_NOT_EXIST);
    }

    public void deleteUser(User user) {
        userRepository.deleteByUser(user);
    }

    public void validateSuwonUniversityEmailForm(String email) {
        int emailLength = email.length();
        int separatorIndex = 0;
        for (int index = 0; index < emailLength; index++) {
            if (email.charAt(index) == '@') {
                separatorIndex = index;
                break;
            }
        }
        if (!email.substring(separatorIndex).equals("@suwon.ac.kr")) {
            throw new CustomException(EMAIL_NOT_VALIDATED);
        }
    }

    @Transactional
    public User softJoin(String loginId, String email, String password, String department) {
        User user = User.builder()
            .loginId(loginId)
            .nickname(nicknameGenerator.generateNickname(department))
            .email(email)
            .password(password)
            .recentUpPost(LocalDateTime.now().minusDays(1))
            .recentEvaluationManner(LocalDateTime.now().minusDays(1))
            .countMannerEvaluation(0L)
            .countTradeAttempt(0L)
            .mannerGrade(BigDecimal.ZERO)
            .status("NOT_AUTH")
            .build();

        userRepository.save(user);
        return user;
    }

    public boolean matchingPassword(Long id, String inputPassword) {
        return bCryptPasswordEncoder.matches(inputPassword,
            userRepository.findById(id).get().getPassword());
    }

    public String initPassword(User requestUser) {
        StringBuilder newPassword = new StringBuilder();
        SecureRandom sr = new SecureRandom();
        sr.setSeed(new Date().getTime());

        char[] charAllSet = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
            'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
            'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '!', '@', '#', '$', '%', '^'};

        char[] charNumberSet = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        char[] charSpecialSet = new char[]{'!', '@', '#', '$', '%', '^'};

        int idx = 0;
        int allLen = charAllSet.length;
        int numberLen = charNumberSet.length;
        int specialLen = charSpecialSet.length;

        // 숫자 최소 1개를 포함하기 위한 반복문
        for (int i = 0; i < 1; i++) {
            idx = sr.nextInt(numberLen);
            newPassword.append(charNumberSet[idx]);
        }
        // 특수문자 최소 1개를 포함하기 위한 반복문
        for (int i = 0; i < 1; i++) {
            idx = sr.nextInt(specialLen);
            newPassword.append(charSpecialSet[idx]);
        }
        for (int i = 0; i < 6; i++) {
            idx = sr.nextInt(allLen);
            newPassword.append(charAllSet[idx]);
        }
        requestUser.encryptPassword(newPassword.toString());
        return newPassword.toString();
    }

    @Transactional
    public boolean isBeforeDay(LocalDateTime requestTime) {
        return requestTime.isBefore(LocalDateTime.now().minusDays(1)) || requestTime == null;
    }
}
