package com.sjy.LitHub.global.security.util;

import com.sjy.LitHub.account.entity.authenum.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    private static final Long TEST_USER_ID = 1L;
    private static final Role TEST_ROLE = Role.ROLE_USER;
    private static final long ACCESS_EXPIRATION = 60000L;
    private static final long REFRESH_EXPIRATION = 120000L; // 120초

    private String accessToken;
    private String refreshToken;

    @BeforeEach
    void setUp() {
        accessToken = jwtUtil.createJwt(AuthConst.TOKEN_TYPE_ACCESS, TEST_USER_ID, TEST_ROLE, ACCESS_EXPIRATION);
        refreshToken = jwtUtil.createJwt(AuthConst.TOKEN_TYPE_REFRESH, TEST_USER_ID, TEST_ROLE, REFRESH_EXPIRATION);
    }

    @Test
    @DisplayName("createJwt() - 토큰이 정상적으로 생성되는지 확인")
    void testCreateJwt() {
        assertNotNull(accessToken);
        assertNotNull(refreshToken);
        assertNotEquals(accessToken, refreshToken);
    }

    @Test
    @DisplayName("getUserId() - JWT 에서 userId를 올바르게 추출")
    void testGetUserId() {
        Long userIdFromToken = jwtUtil.getUserId(accessToken);
        assertEquals(TEST_USER_ID, userIdFromToken);
    }

    @Test
    @DisplayName("getRole() - JWT 에서 역할(Role)을 올바르게 추출")
    void testGetRole() {
        Role roleFromToken = jwtUtil.getRole(accessToken);
        assertEquals(TEST_ROLE, roleFromToken);
    }

    @Test
    @DisplayName("getCategory() - JWT 에서 토큰 카테고리를 올바르게 추출")
    void testGetCategory() {
        String categoryFromToken = jwtUtil.getCategory(accessToken);
        assertEquals(AuthConst.TOKEN_TYPE_ACCESS, categoryFromToken);
    }

    @Test
    @DisplayName("isExpired() - 만료된 토큰을 검증")
    void testIsExpired() throws InterruptedException {
        String shortLivedToken = jwtUtil.createJwt(AuthConst.TOKEN_TYPE_ACCESS, TEST_USER_ID, TEST_ROLE, 1);
        Thread.sleep(10);
        assertTrue(jwtUtil.isExpired(shortLivedToken));
    }

    @Test
    @DisplayName("getExpiration() - 토큰의 남은 만료 시간이 올바르게 계산되는지 확인")
    void testGetExpiration() {
        long expirationTime = jwtUtil.getExpiration(accessToken);
        assertTrue(expirationTime > 0 && expirationTime <= ACCESS_EXPIRATION);
    }

    @Test
    @DisplayName("createJwt() - 토큰이 매번 다른 값을 가지는지 확인")
    void testTokenUniqueness() {
        String token1 = jwtUtil.createJwt(AuthConst.TOKEN_TYPE_ACCESS, TEST_USER_ID, TEST_ROLE, ACCESS_EXPIRATION);
        String token2 = jwtUtil.createJwt(AuthConst.TOKEN_TYPE_ACCESS, TEST_USER_ID, TEST_ROLE, ACCESS_EXPIRATION);

        assertNotEquals(token1, token2);
    }
}