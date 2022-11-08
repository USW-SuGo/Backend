package com.usw.sugo.global.security.authentication;

import com.usw.sugo.domain.user.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public final class UserDetailsImpl implements UserDetails {

    private final Long id;
    private final String nickname;
    private final String loginId;
    private final String email;
    private final String password;
    private final String status;
    private final ArrayList<GrantedAuthority> authorities;

    public UserDetailsImpl(User user, String status, ArrayList<GrantedAuthority> authorities) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.loginId = user.getLoginId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.status = status;
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
