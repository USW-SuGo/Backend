package com.usw.sugo.domain.user.user.service;

import com.usw.sugo.domain.user.entity.User;
import com.usw.sugo.domain.user.user.dto.UserRequestDto.DetailJoinRequest;
import com.usw.sugo.domain.user.user.repository.UserRepository;
import com.usw.sugo.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Date;

import static com.usw.sugo.global.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void validateSuwonAcKrEmail(String email) {

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
    public void validateLoginIdDuplicated(String loginId) {
        if (userRepository.findByLoginId(loginId).isPresent()) {
            throw new CustomException(DUPLICATED_LOGINID);
        }
    }

    @Transactional
    public void validateEmailDuplicated(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new CustomException(DUPLICATED_EMAIL);
        }
    }

    @Transactional
    public User softJoin(DetailJoinRequest detailJoinRequest) {

        User newSoftUser = User.builder()
                .email(detailJoinRequest.getEmail())
                .loginId(detailJoinRequest.getLoginId())
                .password(detailJoinRequest.getPassword())
                .recentUpPost(LocalDateTime.now().minusDays(1))
                .recentEvaluationManner(LocalDateTime.now().minusDays(1))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .countMannerEvaluation(0)
                .countTradeAttempt(0)
                .mannerGrade(BigDecimal.ZERO)
                .status("NOT_AUTH")
                .build();

        userRepository.save(newSoftUser);

        return newSoftUser;
    }

    /*
    입력한 비밀번호와 DB에 담긴 비밀번호를 를 검사하는 로직 (로그인 시 사용함)
    매개변수 : 입력 비밀번호, 비밀번호를 검증하고자 하는 유저 도메인
     */
    @Transactional
    public boolean matchingPassword(Long id, String inputPassword) {
        return bCryptPasswordEncoder.matches(inputPassword, userRepository.findById(id).get().getPassword());
    }

    /**
     * 비밀번호 랜덤값으로 변경
     *
     * @param userId
     * @return
     */
    @Transactional
    public String initPassword(long userId) {
        StringBuilder newPassword = new StringBuilder();
        SecureRandom sr = new SecureRandom();
        sr.setSeed(new Date().getTime());

        char[] charAllSet = new char[]{
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
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

        userRepository.editPassword(userId, newPassword.toString());

        return newPassword.toString();
    }

    @Transactional
    public boolean isBeforeDay(LocalDateTime requestTime) {
        if (requestTime.isBefore(
                LocalDateTime.now()
                        .minusDays(1)) ||
                requestTime == null)
            return true;

        return false;
    }

    @Transactional
    public void isUserExistByUserId(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));
    }

    @Transactional
    public void isUserExistByLoginId(String loginId) {
        userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));
    }
}
