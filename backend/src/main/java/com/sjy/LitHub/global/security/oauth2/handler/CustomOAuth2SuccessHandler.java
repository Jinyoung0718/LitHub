package com.sjy.LitHub.global.security.oauth2.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.sjy.LitHub.account.entity.authenum.Role;
import com.sjy.LitHub.global.config.AppConfig;
import com.sjy.LitHub.global.security.model.UserPrincipal;
import com.sjy.LitHub.global.security.service.TokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getUserId();
        Role role = userPrincipal.getRole();

        if (!response.isCommitted()) {
            if (role == Role.ROLE_GUEST) {
                response.sendRedirect(AppConfig.getSiteFrontUrl() + "/social-signup");
                return;
            }
            tokenService.generateTokensAndSetCookies(response, userId, role);
            response.sendRedirect(AppConfig.getSiteFrontUrl()  + "/");
        }
    }
}