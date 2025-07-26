package com.sjy.LitHub.global.security.support;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.entity.authenum.Role;
import com.sjy.LitHub.account.entity.authenum.Tier;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.global.security.service.TokenService;
import com.sjy.LitHub.global.security.util.AuthConst;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;

@SpringBootTest
@ActiveProfiles("test")
public class TestUserConfig {

	@Autowired
	protected UserRepository userRepository;

	@Autowired
	protected TokenService tokenService;

	protected String testAccessToken;
	protected Long testUserId;

	@PostConstruct
	public void initTestUser() {
		String email = "testuser@example.com";

		Optional<User> existing = userRepository.findByUserEmailAll(email);
		User testUser = existing.orElseGet(() -> {
			User newUser = User.builder()
				.userEmail(email)
				.nickName("테스트유저")
				.password(new BCryptPasswordEncoder().encode("test1234!"))
				.role(Role.ROLE_USER)
				.tier(Tier.SILVER)
				.point(0)
				.build();
			return userRepository.save(newUser);
		});

		this.testUserId = testUser.getId();
		this.testAccessToken = tokenService.generateAccessToken(testUser.getId(), testUser.getRole());
	}

	protected Cookie getAccessTokenCookie() {
		return new Cookie(AuthConst.TOKEN_TYPE_ACCESS, testAccessToken);
	} // 테스트에서 사용할 AccessToken 쿠키를 반환
}