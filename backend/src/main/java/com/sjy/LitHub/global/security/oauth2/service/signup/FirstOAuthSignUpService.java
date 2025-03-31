package com.sjy.LitHub.global.security.oauth2.service.signup;

import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.model.req.signup.SocialSignupDTO;
import com.sjy.LitHub.file.entity.UserGenFile;
import com.sjy.LitHub.file.mapper.UserGenFileMapper;
import com.sjy.LitHub.global.security.model.UserPrincipal;
import com.sjy.LitHub.global.security.oauth2.service.token.OAuthTempTokenService;
import com.sjy.LitHub.global.security.util.AuthConst;
import com.sjy.LitHub.global.security.util.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FirstOAuthSignUpService {

    private final OAuthSignupService oAuthSignupService;
    private final OAuthTempTokenService oAuthTempTokenService;
    private final OAuthUserTempService oAuthUserTempService;
    private final UserGenFileMapper userGenFileMapper;

    @Transactional
    public void finalizeSocialSignup(HttpServletRequest request, HttpServletResponse response, SocialSignupDTO socialSignupDto) {
        Map<String, String> tokenData = oAuthTempTokenService.extractTokenData(request);
        User newUser = oAuthSignupService.processSignup(socialSignupDto, tokenData);

        Map<UserGenFile.TypeCode, UserGenFile> defaultImages = userGenFileMapper.toDefaultUserGenFiles(newUser);
        defaultImages.values().forEach(newUser::addUserGenFile);

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