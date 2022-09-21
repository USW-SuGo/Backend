package com.usw.sugo.domain.majoruser.emailauth.service;

import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.UserEmailAuth;
import com.usw.sugo.domain.majoruser.user.repository.UserRepository;
import com.usw.sugo.domain.majoruser.emailauth.repository.UserEmailAuthRepository;
import com.usw.sugo.domain.status.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserEmailAuthService {

    private final UserRepository userRepository;
    private final UserEmailAuthRepository userEmailAuthRepository;

    public String createEmailAuthToken(Long userId) {

        User user = userRepository.findById(userId).get();

        String payload = UUID.randomUUID().toString();

        UserEmailAuth userEmailAuth = UserEmailAuth.builder()
                .payload(payload)
                .createdAt(LocalDateTime.now())
                .userId(user.getId())
                .status(String.valueOf(Status.NOT_AUTH))
                .build();

        userEmailAuthRepository.save(userEmailAuth);

        return payload;
    }

    public void authorizeToken(String payload) {
        Optional<UserEmailAuth> requestUser = userEmailAuthRepository.findByPayload(payload);

        userEmailAuthRepository.confirmToken(payload);
    }
}
