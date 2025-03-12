package com.sjy.LitHub.global.security.oauth2.service;

import com.sjy.LitHub.account.entity.OAuthUser;
import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.entity.authenum.ProviderInfo;
import com.sjy.LitHub.account.mapper.UserMapper;
import com.sjy.LitHub.account.repository.OAuthUserRepository;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.global.exception.custom.InvalidAuthenticationException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.global.security.model.UserPrincipal;
import com.sjy.LitHub.global.security.oauth2.info.OAuth2UserInfo;
import com.sjy.LitHub.global.security.oauth2.info.OAuth2UserInfoFactory;
import com.sjy.LitHub.global.security.util.AuthConst;
import com.sjy.LitHub.global.security.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Value("${url.base}")
    private String FRONT_URL;

    private final UserRepository userRepository;
    private final OAuthUserRepository oAuthUserRepository;
    private final UserMapper userMapper;
    private final TempTokenService tempTokenService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        ProviderInfo provider = ProviderInfo.from(userRequest.getClientRegistration().getRegistrationId());
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());

        Optional<User> existingUserOpt = userRepository.findByUserEmailAll(oAuth2UserInfo.getEmail());
        return existingUserOpt.map(user -> handleExistingUser(user, oAuth2UserInfo, provider))
                .orElseGet(() -> handleNewUser(oAuth2UserInfo, provider));
    }

    private OAuth2User handleExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo, ProviderInfo provider) {
        if (existingUser.getDeletedAt() != null) {
            throw new InvalidAuthenticationException(BaseResponseStatus.USER_LOGIN_RECOVERY_REQUIRED);
        }

        if (!oAuthUserRepository.existsByUserAndProvider(existingUser, provider)) {
            OAuthUser newOAuthUser = userMapper.ofOAuthAccountForExistingUser(existingUser, oAuth2UserInfo, provider);
            oAuthUserRepository.save(newOAuthUser);
        }

        return new UserPrincipal(existingUser.getId(), existingUser.getPassword(), existingUser.getRole());
    }

    private OAuth2User handleNewUser(OAuth2UserInfo oAuth2UserInfo, ProviderInfo provider) {
        String tempToken = tempTokenService.createTempSignupToken(
                oAuth2UserInfo.getEmail(),
                provider.name(),
                oAuth2UserInfo.getProviderId(),
                600000L
        );

        if (!sendTempTokenAndRedirect(tempToken)) {
            cleanupFailedSignup(oAuth2UserInfo.getEmail(), provider);
            throw new InvalidAuthenticationException(BaseResponseStatus.OAUTH_REDIRECT_FAILED);
        }

        return null;
    }

    private boolean sendTempTokenAndRedirect(String tempToken) {
        HttpServletResponse response = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
        if (response == null) return false;
        response.addCookie(CookieUtil.createCookie(AuthConst.TOKEN_TYPE_TEMP, tempToken, 10 * 60));

        try {
            response.sendRedirect(FRONT_URL + "/social-signup");
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void cleanupFailedSignup(String email, ProviderInfo provider) {
        oAuthUserRepository.findByUserEmailAndProvider(email, provider)
                .ifPresent(oAuthUserRepository::delete);
    }
}