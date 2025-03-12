package com.sjy.LitHub.global.security.model;

import com.sjy.LitHub.account.entity.authenum.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class UserPrincipal implements UserDetails, OAuth2User {

    private final Long userId;
    private final String password;
    private final Role role;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long userId, String password, Role role) {
        this.userId = userId;
        this.password = password;
        this.role = role;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(role.getAuthority()));
    }

    public UserPrincipal(Long userId, Role role) {
        this.userId = userId;
        this.password = null;
        this.role = role;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(role.getAuthority()));
    }

    @Override
    public String getUsername() {
        return String.valueOf(userId);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.emptyMap();
    }

    @Override
    public String getName() {
        return String.valueOf(userId);
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