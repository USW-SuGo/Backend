package com.usw.sugo.global.security.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
        String userEmail = (String) token.getPrincipal();
        Long userId = (Long) token.getCredentials();

        System.out.println("token = " + token);

        // UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        return new JwtAuthenticationToken(userEmail, userId);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
