package com.usw.sugo.global.security.authentication;

import lombok.Generated;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import java.util.Objects;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private Object credentials;

    public JwtAuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        credentials = null;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(true);
    }

    @Generated
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JwtAuthenticationToken)) return false;
        if (!super.equals(o)) return false;
        JwtAuthenticationToken that = (JwtAuthenticationToken) o;
        return Objects.equals(principal, that.principal);
    }

    @Generated
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), principal);
    }
}
