package com.sjy.LitHub.global.security.filter;

import com.sjy.LitHub.TestContainerConfig;
import com.sjy.LitHub.account.entity.authenum.Role;
import com.sjy.LitHub.global.exception.custom.InvalidTokenException;
import com.sjy.LitHub.global.security.service.TokenService;
import com.sjy.LitHub.global.security.util.AuthConst;
import com.sjy.LitHub.global.security.util.JwtUtil;
import com.sjy.LitHub.global.security.util.RedisBlacklistUtil;
import com.sjy.LitHub.global.security.util.RedisRefreshTokenUtil;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@AutoConfigureMockMvc
public class JwtFilterTest extends TestContainerConfig {

    @Autowired
    private RedisBlacklistUtil redisBlacklistUtil;

    @Autowired
    private RedisRefreshTokenUtil redisRefreshTokenUtil;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    private JwtFilter jwtFilter;

    @Autowired
    @Qualifier("StringRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    private String validAccessToken;
    private String expiredAccessToken;
    private String validRefreshToken;
    private String blacklistedToken;
    private String invalidToken;
    private static final String TEST_INVALID_REFRESH_TOKEN = "invalid-refresh-token";

    @BeforeEach
    public void setUp() {
        jwtFilter = new JwtFilter(redisBlacklistUtil, tokenService, jwtUtil);

        Long testUserId = 1L;
        Role testUserRole = Role.ROLE_USER;

        validAccessToken = jwtUtil.createJwt(AuthConst.TOKEN_TYPE_ACCESS, testUserId, testUserRole, AuthConst.ACCESS_EXPIRATION);
        validRefreshToken = jwtUtil.createJwt(AuthConst.TOKEN_TYPE_REFRESH, testUserId, testUserRole, AuthConst.REFRESH_EXPIRATION);
        expiredAccessToken = jwtUtil.createJwt(AuthConst.TOKEN_TYPE_ACCESS, testUserId, testUserRole, 1);

        blacklistedToken = jwtUtil.createJwt(AuthConst.TOKEN_TYPE_ACCESS, testUserId + 1, testUserRole, AuthConst.ACCESS_EXPIRATION);
        redisBlacklistUtil.addToBlacklist(blacklistedToken, AuthConst.ACCESS_EXPIRATION);
        redisRefreshTokenUtil.addRefreshToken(testUserId, validRefreshToken, AuthConst.REFRESH_EXPIRATION);

        invalidToken = "invalid.jwt.token";
    }

    @AfterEach
    public void tearDown() {
        if (redisTemplate != null && redisTemplate.getConnectionFactory() != null) {
            try (RedisConnection connection = redisTemplate.getConnectionFactory().getConnection()) {
                connection.serverCommands().flushAll();
            }
        }
    }

    @Test
    @DisplayName("정상적인 액세스 토큰이 있을 경우 요청 통과")
    public void testValidAccessToken() throws Exception {
        Cookie accessTokenCookie = new Cookie(AuthConst.TOKEN_TYPE_ACCESS, validAccessToken);
        System.out.println("🚀 [TEST] 액세스 토큰 쿠키 값: " + accessTokenCookie.getValue());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/me")
                        .cookie(accessTokenCookie)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("JWT 필터 - 유효하지 않은 리프레시 토큰 요청 시 예외가 적절히 처리되는지 확인")
    void testJwtFilterWithInvalidRefreshToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(AuthConst.TOKEN_TYPE_REFRESH, TEST_INVALID_REFRESH_TOKEN));

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        jwtFilter.doFilter(request, response, filterChain);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertNotNull(request.getAttribute("exception"));
        assertInstanceOf(InvalidTokenException.class, request.getAttribute("exception"));
    }

    @Test
    @DisplayName("만료된 토큰이면 리프레시 토큰을 사용하여 새 토큰 발급 및 요청 허용")
    public void testExpiredToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/me")
                        .cookie(new Cookie(AuthConst.TOKEN_TYPE_ACCESS, expiredAccessToken))
                        .cookie(new Cookie(AuthConst.TOKEN_TYPE_REFRESH, validRefreshToken))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("블랙리스트에 등록된 토큰 사용 시 401 Unauthorized 반환")
    public void testBlacklistedToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/me")
                        .cookie(new Cookie(AuthConst.TOKEN_TYPE_ACCESS, blacklistedToken))
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("변조된 토큰 사용 시 401 Unauthorized 반환")
    public void testInvalidToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/me")
                        .cookie(new Cookie(AuthConst.TOKEN_TYPE_ACCESS, invalidToken))
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    System.out.println("응답 본문: " + responseBody);
                });
    }

    @Test
    @DisplayName("액세스 토큰 없이 요청 시 401 Unauthorized 반환")
    public void testRequestWithoutAccessToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/me")
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    System.out.println("응답 본문: " + responseBody);
                });
    }

    @Test
    @DisplayName("만료된 액세스 토큰만 있고 리프레시 토큰이 없을 경우 401 Unauthorized 반환")
    public void testExpiredAccessTokenWithoutRefreshToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/me")
                        .cookie(new Cookie(AuthConst.TOKEN_TYPE_ACCESS, expiredAccessToken))
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    System.out.println("응답 본문: " + responseBody);
                });
    }

    @Test
    @DisplayName("잘못된 형식의 액세스 토큰 처리")
    public void testInvalidFormatAccessToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/me")
                        .cookie(new Cookie(AuthConst.TOKEN_TYPE_ACCESS, "invalid-format-token"))
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    System.out.println("응답 본문: " + responseBody);
                });
    }

    @Test
    @DisplayName("만료된 액세스 토큰과 유효한 리프레시 토큰으로 새 토큰 발급")
    public void testValidRefreshTokenForNewAccessToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/me")
                        .cookie(new Cookie(AuthConst.TOKEN_TYPE_ACCESS, expiredAccessToken))
                        .cookie(new Cookie(AuthConst.TOKEN_TYPE_REFRESH, validRefreshToken))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    System.out.println("응답 본문: " + responseBody);
                });
    }

}