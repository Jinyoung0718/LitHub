package com.sjy.LitHub.global.security.oauth2.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import com.sjy.LitHub.TestContainerConfig;
import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.model.req.signup.SocialSignupDTO;
import com.sjy.LitHub.account.repository.OAuthUserRepository;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.security.util.AuthConst;

import jakarta.servlet.http.Cookie;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@AutoConfigureMockMvc
public class FirstOAuthSignUpServiceTest extends TestContainerConfig {

    @Autowired
    private TempTokenService tempTokenService;

    @Autowired
    private OAuthTempTokenService oAuthTempTokenService;

    @Autowired
    private OAuthSignupService oAuthSignupService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OAuthUserRepository oAuthUserRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @Qualifier("StringRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    private String validTempToken;
    private String expiredTempToken;
    private final String invalidTempToken = "invalid.token.format";

    @BeforeEach
    public void setUp() {

        if (redisTemplate != null && redisTemplate.getConnectionFactory() != null) {
            try (RedisConnection connection = redisTemplate.getConnectionFactory().getConnection()) {
                connection.serverCommands().flushAll();
            }
        }
        oAuthUserRepository.deleteAll();
        userRepository.deleteAll();

        validTempToken = tempTokenService.createTempSignupToken(
            "test@example.com",
            "GOOGLE",
            "provider123",
            60000 // 1분 유효
        );

        expiredTempToken = tempTokenService.createTempSignupToken(
            "expired@example.com",
            "GOOGLE",
            "providerExpired",
            1 // 즉시 만료
        );
    }

    @Test
    @DisplayName("정상적인 임시 토큰으로 회원가입 요청")
    public void testValidSocialSignup() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/social-signup")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie(AuthConst.TOKEN_TYPE_TEMP, validTempToken))
                .content(new ObjectMapper().writeValueAsString(
                    new SocialSignupDTO("닉네임3", "password123!")
                )))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("OAuthSignupService - 중복 닉네임으로 가입 시 예외 발생")
    public void testDuplicateNickname() {
        Map<String, String> tokenData = tempTokenService.validateTempSignupToken(validTempToken);
        SocialSignupDTO signupDto = new SocialSignupDTO("닉네임2", "password123!");
        oAuthSignupService.processSignup(signupDto, tokenData);
        assertThrows(InvalidUserException.class, () -> oAuthSignupService.processSignup(signupDto, tokenData));
    }

    @Test
    @DisplayName("임시 토큰이 정상적으로 생성 및 검증되는지 테스트")
    public void testValidTempToken() {
        Map<String, String> tokenData = tempTokenService.validateTempSignupToken(validTempToken);
        assertEquals("test@example.com", tokenData.get(AuthConst.TEMP_USER_EMAIL));
        assertEquals("GOOGLE", tokenData.get(AuthConst.TEMP_PROVIDER));
        assertEquals("provider123", tokenData.get(AuthConst.TEMP_PROVIDER_ID));
    }

    @Test
    @DisplayName("만료된 임시 토큰 검증 시 예외 발생")
    public void testExpiredTempToken() {
        assertThrows(InvalidUserException.class, () ->
                tempTokenService.validateTempSignupToken(expiredTempToken),
            "만료된 토큰 사용 시 예외가 발생해야 함"
        );
    }

    @Test
    @DisplayName("변조된 임시 토큰 검증 시 예외 발생")
    public void testInvalidTempToken() {
        assertThrows(InvalidUserException.class, () ->
                tempTokenService.validateTempSignupToken(invalidTempToken),
            "잘못된 토큰 사용 시 예외가 발생해야 함"
        );
    }

    @Test
    @DisplayName("임시 토큰 없이 회원가입 요청 시 예외 발생")
    public void testSignupWithoutTempToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/social-signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new SocialSignupDTO("nickname", "password"))))
            .andExpect(status().isBadRequest())
            .andExpect(result -> {
                String responseBody = result.getResponse().getContentAsString();
                System.out.println("응답 본문: " + responseBody);
            });
    }

    @Test
    @DisplayName("OAuthTempTokenService - 유효한 임시 토큰에서 데이터 추출 테스트")
    public void testExtractTokenData() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(AuthConst.TOKEN_TYPE_TEMP, validTempToken));

        Map<String, String> tokenData = oAuthTempTokenService.extractTokenData(request);
        assertEquals("test@example.com", tokenData.get(AuthConst.TEMP_USER_EMAIL));
        assertEquals("GOOGLE", tokenData.get(AuthConst.TEMP_PROVIDER));
        assertEquals("provider123", tokenData.get(AuthConst.TEMP_PROVIDER_ID));
    }

    @Test
    @DisplayName("OAuthTempTokenService - 임시 토큰 없이 요청 시 예외 발생")
    public void testExtractTokenDataWithoutToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        assertThrows(InvalidUserException.class, () -> oAuthTempTokenService.extractTokenData(request));
    }

    @Test
    @DisplayName("OAuthSignupService - 회원가입 시 정상적으로 저장되는지 테스트")
    public void testProcessSignup() {
        Map<String, String> tokenData = tempTokenService.validateTempSignupToken(validTempToken);
        SocialSignupDTO signupDto = new SocialSignupDTO("닉네임1", "password123!");

        User newUser = oAuthSignupService.processSignup(signupDto, tokenData);

        assertNotNull(newUser);
        assertEquals("test@example.com", newUser.getUserEmail());
        assertEquals("닉네임1", newUser.getNickName());
        assertTrue(userRepository.existsById(newUser.getId()));
    }
}