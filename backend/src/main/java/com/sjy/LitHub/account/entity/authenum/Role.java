package com.sjy.LitHub.account.entity.authenum;

import lombok.Getter;

@Getter
public enum Role {
    ROLE_USER,
    ROLE_ADMIN,
    ROLE_GUEST;

    public String getAuthority() {
        return name();
    }
}