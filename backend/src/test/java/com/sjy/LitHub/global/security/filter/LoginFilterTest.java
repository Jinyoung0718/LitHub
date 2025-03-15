package com.sjy.LitHub.global.security.filter;

import com.sjy.LitHub.TestContainerConfig;
import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.global.security.util.AuthConst;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@AutoConfigureMockMvc
class LoginFilterTest extends TestContainerConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        String rawPassword = "korean12@";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        System.out.println("Encoded Password: " + encodedPassword); // 인코딩된 비밀번호 출력

        userRepository.save(User.builder()
                .userEmail("1234@naver.com")
                .nickName("existingNickName")
                .password(encodedPassword)
                .profileImageUrlSmall("https://example.com/default-small.png")
                .profileImageUrlLarge("https://example.com/default-large.png")
                .build());

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("비밀번호 검증 테스트")
    void testPasswordEncoding() {
        User user = userRepository.findByUserEmailActive("1234@naver.com")
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("Stored Encoded Password: " + user.getPassword());
        assertTrue(passwordEncoder.matches("korean12@", user.getPassword()));
    }

    @Test
    @DisplayName("정상적인 로그인 요청")
    void testSuccessfulLogin() throws Exception {
        Map<String, String> loginRequest = Map.of(
                "username", "1234@naver.com",
                "password", "korean12@"
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/basic/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(AuthConst.TOKEN_TYPE_ACCESS))
                .andExpect(cookie().exists(AuthConst.TOKEN_TYPE_REFRESH));
    }


    @Test
    @DisplayName("잘못된 비밀번호 로그인 요청")
    void testInvalidPasswordLogin() throws Exception {
        Map<String, String> loginRequest = Map.of(
                "username", "1234@naver.com",
                "password", "wrongPassword"
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/basic/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("존재하지 않는 이메일 로그인 요청")
    void testNonExistentUserLogin() throws Exception {
        Map<String, String> loginRequest = Map.of(
                "username", "nonexistent@example.com",
                "password", "korean12@"
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/basic/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("논리 삭제된 계정으로 로그인 요청")
    void testDeletedUserLogin() throws Exception {
        User deletedUser = User.builder()
                .userEmail("deleted@example.com")
                .nickName("deletedNick")
                .password("korean12@")
                .deletedAt(LocalDateTime.now())
                .profileImageUrlSmall("https://example.com/default-small.png")
                .profileImageUrlLarge("https://example.com/default-large.png")
                .build();

        userRepository.save(deletedUser);

        Map<String, String> loginRequest = Map.of(
                "username", "deleted@example.com",
                "password", "korean12@"
        );

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/basic/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        System.out.println("🔍 응답 본문: " + responseBody);
    }

    @Test
    @DisplayName("요청 본문이 비어있을 경우")
    void testEmptyRequestBody() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/basic/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(BaseResponseStatus.UNAUTHORIZED.getMessage()));
    }
}