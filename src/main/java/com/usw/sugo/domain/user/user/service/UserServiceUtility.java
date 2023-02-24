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
import java.util.List;
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

    private static final List<Character> charAllSet = List.of(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
        'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
        'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '!', '@', '#', '$', '%', '^'
    );

    public static final List<Character> charNumberSet = List.of(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    );

    private static final List<Character> charSpecialSet = List.of(
        '!', '@', '#', '$', '%', '^'
    );

    private final int allLen = charAllSet.size();
    private final int numberLen = charNumberSet.size();
    private final int specialLen = charSpecialSet.size();

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
        final int emailLength = email.length();
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
    public User softJoin(
        String loginId, String email, String password, String department, Boolean pushAlarmStatus,
        String fcmToken
    ) {
        final User user = User.builder()
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
            .pushAlarmStatus(pushAlarmStatus)
            .fcmToken(fcmToken)
            .build();

        userRepository.save(user);
        return user;
    }

    public boolean matchingPassword(Long id, String inputPassword) {
        return bCryptPasswordEncoder.matches(inputPassword,
            userRepository.findById(id).get().getPassword());
    }

    public String initPassword(User requestUser) {
        final StringBuilder newPassword = new StringBuilder();
        final SecureRandom sr = new SecureRandom();
        sr.setSeed(new Date().getTime());

        int index = 0;
        // 숫자 최소 1개를 포함하기 위한 반복문
        for (int i = 0; i < 1; i++) {
            index = sr.nextInt(numberLen);
            newPassword.append(charNumberSet.get(index));
        }
        // 특수문자 최소 1개를 포함하기 위한 반복문
        for (int i = 0; i < 1; i++) {
            index = sr.nextInt(specialLen);
            newPassword.append(charSpecialSet.get(index));
        }
        for (int i = 0; i < 6; i++) {
            index = sr.nextInt(allLen);
            newPassword.append(charAllSet.get(index));
        }
        requestUser.encryptPassword(newPassword.toString());
        return newPassword.toString();
    }

    @Transactional
    public boolean isBeforeDay(LocalDateTime requestTime) {
        return requestTime.isBefore(LocalDateTime.now().minusDays(1)) || requestTime == null;
    }
}
