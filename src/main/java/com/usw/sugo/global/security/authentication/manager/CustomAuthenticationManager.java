package com.usw.sugo.global.security.authentication.manager;

import java.util.ArrayList;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationManager implements AuthenticationManager {

    @Override
    public Authentication authenticate(
        Authentication authentication
    ) throws AuthenticationException {
        final UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(
                authentication.getPrincipal(), authentication.getCredentials()
            );

        final ArrayList<GrantedAuthority> grantedAuths = new ArrayList<>();
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_AVAILABLE"));

        return new UsernamePasswordAuthenticationToken(
            authenticationToken.getPrincipal(), authenticationToken.getCredentials(), grantedAuths);
    }
}
