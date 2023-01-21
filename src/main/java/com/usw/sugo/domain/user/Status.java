package com.usw.sugo.domain.user;

import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

@ToString
public enum Status implements GrantedAuthority {

    AVAILABLE("ROLE_AVAILABLE", "유저"),
    NOT_AUTH("ROLE_NOT_AUTH", "비인증"),
    SLEEPING("ROLE_SLEEPING", "휴면"),
    RESTRICTED("ROLE_RESTRICTED", "제제"),
    ADMIN("ROLE_ADMIN", "관리자");


    private String authority;
    private String description;

    Status (String authority, String description){
        this.authority = authority;
        this.description = description;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public String getDescription() {
        return description;
    }
}
