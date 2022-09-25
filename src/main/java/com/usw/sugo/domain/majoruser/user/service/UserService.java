package com.usw.sugo.domain.majoruser.user.service;

import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.user.repository.UserRepository;
import com.usw.sugo.domain.status.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

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

    /*
    입력한 비밀번호와 DB에 담긴 비밀번호를 를 검사하는 로직 (로그인 시 사용함)
    매개변수 : 입력 비밀번호, 비밀번호를 검증하고자 하는 유저 도메인
     */
    @Transactional
    public boolean matchingPassword(Long id, String inputPassword) {
        return bCryptPasswordEncoder.matches(inputPassword, userRepository.findById(id).get().getPassword());
    }
}
