package com.sjy.LitHub.global.util;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.entity.authenum.Role;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.global.security.model.UserPrincipal;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUser {

    public static User getAuthUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new InvalidUserException(BaseResponseStatus.AUTH_CHECK_FAILED);
        }

        Object principal = authentication.getPrincipal();
        UserPrincipal user = (UserPrincipal) principal;

        return User.builder()
            .id(Long.parseLong(user.getUsername()))
            .role(Role.valueOf(user.getRole().name()))
            .build();
    }

    public static Long getUserId() {
        return getAuthUser().getId();
    }
}
