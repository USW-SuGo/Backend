package com.usw.sugo.global.security.authentication;

import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.user.repository.UserDetailsRepository;
import com.usw.sugo.exception.CustomException;
import com.usw.sugo.exception.ErrorCode;

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
    public UserDetailsImpl loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST));
        return new UserDetailsImpl(user.getId(), user.getNickname(), user, new ArrayList<>());
    }
}
