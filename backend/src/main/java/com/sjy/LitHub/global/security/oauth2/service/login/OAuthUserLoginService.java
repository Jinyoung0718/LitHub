package com.sjy.LitHub.global.security.oauth2.service.login;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.account.entity.OAuthUser;
import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.entity.authenum.ProviderInfo;
import com.sjy.LitHub.account.mapper.UserMapper;
import com.sjy.LitHub.account.repository.OAuthUserRepository;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.global.security.model.UserPrincipal;
import com.sjy.LitHub.global.security.oauth2.info.OAuth2UserInfo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthUserLoginService {

	private final UserRepository userRepository;
	private final OAuthUserRepository oAuthUserRepository;
	private final UserMapper userMapper;

	@Transactional
	public UserPrincipal restoreAndLinkOAuthUser(User existingUser, OAuth2UserInfo userInfo, ProviderInfo provider) {

		if (existingUser.getDeletedAt() != null) {
			userRepository.restoreUserByEmail(existingUser.getUserEmail());
		}

		if (!oAuthUserRepository.existsByUserAndProvider(existingUser, provider)) {
			OAuthUser newOAuthUser = userMapper.ofOAuthAccountForExistingUser(existingUser, userInfo, provider);
			oAuthUserRepository.save(newOAuthUser);
		}

		return new UserPrincipal(existingUser.getId(), existingUser.getPassword(), existingUser.getRole());
	}

	@Transactional
	public void cleanupFailedSignup(String email, ProviderInfo provider) {
		oAuthUserRepository.findByUserEmailAndProvider(email, provider)
			.ifPresent(oAuthUserRepository::delete);
	}
}