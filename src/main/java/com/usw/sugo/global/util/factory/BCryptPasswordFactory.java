package com.usw.sugo.global.util.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BCryptPasswordFactory {

    private static final BCryptPasswordEncoder B_CRYPT_PASSWORD_ENCODER = new BCryptPasswordEncoder();

    public static BCryptPasswordEncoder getBCryptPasswordEncoder() {
        return B_CRYPT_PASSWORD_ENCODER;
    }
}
