package com.usw.sugo.global.security.authentication;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CustomAuthenticationManager implements AuthenticationManager {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        ArrayList<GrantedAuthority> grantedAuths = new ArrayList<>();

        grantedAuths.add(new SimpleGrantedAuthority("ROLE_AVAILABLE"));
        Authentication customAuthentication = new UsernamePasswordAuthenticationToken(
                authentication.getPrincipal(), authentication.getCredentials(), grantedAuths);

        // System.out.println("customAuthentication = " + customAuthentication);

        return customAuthentication;
    }
}
