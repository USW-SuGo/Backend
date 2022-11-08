package com.usw.sugo.global.security.authentication;

import com.usw.sugo.domain.user.User;
import com.usw.sugo.domain.user.user.repository.UserDetailsRepository;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.exception.ErrorCode;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserDetailsRepository userRepository;

    public UserDetailsServiceImpl(UserDetailsRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public UserDetailsImpl loadUserByUsername(String loginId) throws UsernameNotFoundException {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST));
        return new UserDetailsImpl(user, user.getStatus().toString(), new ArrayList<>());
    }
}
