package com.usw.sugo.domain.majoruser.user.service;

import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.user.dto.UserRequestDto;
import com.usw.sugo.domain.majoruser.user.dto.UserRequestDto.DetailJoinRequest;
import com.usw.sugo.domain.majoruser.user.repository.UserRepository;
import com.usw.sugo.domain.status.Status;
import com.usw.sugo.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static com.usw.sugo.global.exception.ErrorCode.NOT_AUTHORIZED_EMAIL;
import static com.usw.sugo.global.exception.ErrorCode.USER_ALREADY_JOIN;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 이메일 인증을 하지 않은 회원가입 요청 유저
    @Transactional
    public User softSaveUser(String email) {
        User user = User.builder()
                .email(email)
                .status(Status.NOT_AUTH)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return user;
    }

    @Transactional
    public void realJoin(User requestUser, DetailJoinRequest detailJoinRequest) {
        // 이미 회원가입을 수행한 유저일 때
        if (requestUser.getNickname() != null) throw new CustomException(USER_ALREADY_JOIN);

        // User 가 DB에 존재하고, 이메일 인증을 받았을 때
        if (!requestUser.getStatus().equals(Status.AVAILABLE)) throw new CustomException(NOT_AUTHORIZED_EMAIL);

        // 이메일 인증은 아직 안받았을 때
        else if (requestUser.getStatus().equals(Status.AVAILABLE)) {
            Long userId = requestUser.getId();

            // 비밀번호 암호화, 닉네임 발급 -> 최종 회원가입 처리
            userRepository.detailJoin(detailJoinRequest, userId);
            // 유저 변경 시각 타임스탬프
            userRepository.setModifiedDate(userId);
        }
    }

    /*
    입력한 비밀번호와 DB에 담긴 비밀번호를 를 검사하는 로직 (로그인 시 사용함)
    매개변수 : 입력 비밀번호, 비밀번호를 검증하고자 하는 유저 도메인
     */
    @Transactional
    public boolean matchingPassword(Long id, String inputPassword) {
        return bCryptPasswordEncoder.matches(inputPassword, userRepository.findById(id).get().getPassword());
    }

    @Transactional
    public boolean isBeforeDay(LocalDateTime requestTime) {
        if (requestTime.isBefore(LocalDateTime.now().minusDays(1))
                || requestTime == null) return true;

        return false;
    }
}
