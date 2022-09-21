package com.usw.sugo.global.security;

import com.usw.sugo.domain.majoruser.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public final class UserDetailsImpl implements UserDetails {

    private final Long id;
    private final String nickname;
    private final String email;
    private final String password;
    private final ArrayList<GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String nickname, User user, ArrayList<GrantedAuthority> authorities) {
        this.id = id;
        this.nickname = nickname;
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>(authorities);
        return authList;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
