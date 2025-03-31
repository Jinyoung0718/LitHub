package com.sjy.LitHub.global.security.oauth2.service.signup;

import com.sjy.LitHub.account.entity.OAuthUser;
import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.entity.authenum.ProviderInfo;
import com.sjy.LitHub.account.mapper.UserMapper;
import com.sjy.LitHub.account.model.req.signup.SocialSignupDTO;
import com.sjy.LitHub.account.repository.OAuthUserRepository;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.account.service.auth.AuthService;
import com.sjy.LitHub.account.util.PasswordManager;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.global.security.util.AuthConst;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuthSignupService {

    private final UserRepository userRepository;
    private final OAuthUserRepository oAuthUserRepository;
    private final UserMapper userMapper;
    private final PasswordManager passwordManager;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @Transactional
    public User processSignup(SocialSignupDTO socialSignupDto, Map<String, String> tokenData) {
        String encodedPassword = passwordEncoder.encode(socialSignupDto.getUserPassword());
        validateUserInput(socialSignupDto, encodedPassword);
        User newUser = createUserEntity(socialSignupDto, tokenData, encodedPassword);
        saveOAuthUser(newUser, tokenData);
        return newUser;
    }

    private void validateUserInput(SocialSignupDTO socialSignupDto, String encodedPassword) {
        authService.validateNicknameAvailability(socialSignupDto.getNickName());
        if (!passwordManager.isInvalid(encodedPassword)) {
            throw new InvalidUserException(BaseResponseStatus.USER_PASSWORD_NOT_VALID);
        }
    }

    private User createUserEntity(SocialSignupDTO socialSignupDto, Map<String, String> tokenData, String encodedPassword) {
        String email = tokenData.get(AuthConst.TEMP_USER_EMAIL);
        return userRepository.save(userMapper.ofSocialSignupDTO(socialSignupDto, encodedPassword, email));
    }

    private void saveOAuthUser(User newUser, Map<String, String> tokenData) {
        ProviderInfo provider = ProviderInfo.valueOf(tokenData.get(AuthConst.TEMP_PROVIDER));
        String providerId = tokenData.get(AuthConst.TEMP_PROVIDER_ID);
        OAuthUser newOAuthUser = userMapper.ofOAuthSignupComplete(newUser, provider, providerId);
        oAuthUserRepository.save(newOAuthUser);
    }
}