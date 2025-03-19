package com.sjy.LitHub.global.security.oauth2.service;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.model.req.signup.SocialSignupDTO;
import com.sjy.LitHub.global.security.model.UserPrincipal;
import com.sjy.LitHub.global.security.util.AuthConst;
import com.sjy.LitHub.global.security.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FirstOAuthSignUpService {

    private final OAuthSignupService oAuthSignupService;
    private final OAuthTempTokenService oAuthTempTokenService;
    private final OAuthUserTempService oAuthUserTempService;

    @Transactional
    public void finalizeSocialSignup(HttpServletRequest request, HttpServletResponse response, SocialSignupDTO socialSignupDto) {
        Map<String, String> tokenData = oAuthTempTokenService.extractTokenData(request);
        User newUser = oAuthSignupService.processSignup(socialSignupDto, tokenData);

        UserPrincipal userPrincipal = new UserPrincipal(newUser.getId(), newUser.getRole());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userPrincipal,
            null,
            userPrincipal.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        oAuthTempTokenService.generateAndSetTokens(response, newUser);
        oAuthUserTempService.deleteTempOAuthUser(tokenData.get(AuthConst.TEMP_USER_EMAIL));
        response.addCookie(CookieUtil.deleteCookie(AuthConst.TOKEN_TYPE_TEMP));
    }
}