package com.sjy.LitHub.account.service.auth;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.TestContainerConfig;
import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.entity.authenum.Role;
import com.sjy.LitHub.account.model.req.NicknameRequestDTO;
import com.sjy.LitHub.account.model.req.signup.SignupDTO;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.account.service.UserInfo.MyPageService;
import com.sjy.LitHub.account.service.email.RedisEmailService;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.BaseResponseStatus;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class AuthServiceTest extends TestContainerConfig {

    @Autowired
    private AuthService authService;

    @Autowired
    private MyPageService myPageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RedisEmailService redisEmailService;

    @AfterEach
    public void tearDown() {
        List<String> testEmails = Arrays.asList(
                "testuser@example.com",
                "passwordinvalid@example.com",
                "duplicatenickname@example.com",
                "existinguser@example.com",
                "deleteduser@example.com"
        );

        for (String email : testEmails) {
            redisEmailService.deleteData("emailAuth:" + email + ":verified");
            redisEmailService.deleteData("emailAuth:" + email + ":requestLock");
        }
    }

    @Test
    @DisplayName("회원가입 테스트 - 이메일 인증 필수")
    public void testSignup_EmailNotVerified() {
        SignupDTO signupDto = new SignupDTO(
                "testNickName",
                "testuser@example.com",
                "validPassword123"
        );

        InvalidUserException thrown = assertThrows(InvalidUserException.class, () -> authService.signup(signupDto));
        assertEquals(BaseResponseStatus.EMAIL_VERIFICATION_REQUIRED, thrown.getStatus());
    }

    @Test
    @DisplayName("회원가입 테스트 - 비밀번호 유효성 검사 실패")
    public void testSignup_PasswordInvalid() {
        SignupDTO signupDto = new SignupDTO(
                "testNickName",
                "passwordinvalid@example.com",
                "weak"
        );

        redisEmailService.setData("emailAuth:passwordinvalid@example.com:verified", "true", 600);
        InvalidUserException thrown = assertThrows(InvalidUserException.class, () -> authService.signup(signupDto));
        assertEquals(BaseResponseStatus.USER_PASSWORD_INVALID, thrown.getStatus());
    }

    @Test
    @DisplayName("회원가입 테스트 - 중복된 닉네임 검사")
    public void testSignup_NicknameDuplicate() {
        userRepository.save(User.builder()
                .userEmail("existinguser@example.com")
                .nickName("existingNickName")
                .password("korean12@")
                .profileImageUrlSmall("https://example.com/default-small.png")
                .profileImageUrlLarge("https://example.com/default-large.png")
                .build());

        SignupDTO signupDto = new SignupDTO(
                "existingNickName",
                "duplicatenickname@example.com",
                "korean12@"
        );

        redisEmailService.setData("emailAuth:duplicatenickname@example.com:verified", "true", 600);
        InvalidUserException thrown = assertThrows(InvalidUserException.class, () -> authService.signup(signupDto));
        assertEquals(BaseResponseStatus.USER_NICKNAME_DUPLICATE, thrown.getStatus());
    }


    @Test
    @DisplayName("회원가입 테스트 - 이미 존재하는 사용자")
    public void testSignup_UserAlreadyExists() {
        userRepository.save(User.builder()
                .userEmail("existinguser@example.com")
                .nickName("testNickName1")
                .password("validPassword123")
                .profileImageUrlSmall("https://example.com/default-small.png")
                .profileImageUrlLarge("https://example.com/default-large.png")
                .build());

        SignupDTO signupDto = new SignupDTO(
                "testNickName2",
                "existinguser@example.com",
                "korean12@"
        );

        redisEmailService.setData("emailAuth:existinguser@example.com:verified", "true", 600);
        InvalidUserException thrown = assertThrows(InvalidUserException.class, () -> authService.signup(signupDto));
        assertEquals(BaseResponseStatus.USER_ALREADY_EXISTS, thrown.getStatus());
    }


    @Test
    @DisplayName("회원가입 테스트 - 이메일 인증 완료 후 정상 가입")
    public void testSignup_Success() {
        SignupDTO signupDto = new SignupDTO(
                "testNickName",
                "testuser@example.com",
                "korean12@"
        );

        redisEmailService.setData("emailAuth:testuser@example.com:verified", "true", 600);
        authService.signup(signupDto);
        User savedUser = userRepository.findByUserEmailAll(signupDto.getUserEmail())
                .orElseThrow(() -> new InvalidUserException(BaseResponseStatus.USER_NOT_FOUND));

        assertNotNull(savedUser);
        assertEquals(signupDto.getNickName(), savedUser.getNickName());
    }

    @Test
    @DisplayName("사용자 복구 테스트")
    public void testRestoreUser() {
        String userEmail = "deleteduser@example.com";
        User user = User.builder()
                .userEmail(userEmail)
                .nickName("testNickName")
                .password("password123")
                .profileImageUrlSmall("smallImage")
                .profileImageUrlLarge("largeImage")
                .role(Role.ROLE_USER)
                .deletedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
        userRepository.flush();

        User beforeRestore = userRepository.findByUserEmailAll(userEmail)
                .orElseThrow(() -> new InvalidUserException(BaseResponseStatus.USER_NOT_FOUND));
        assertNotNull(beforeRestore.getDeletedAt());
        authService.restoreUser(userEmail);

        entityManager.flush();
        entityManager.clear();

        User restoredUser = userRepository.findByUserEmailActive(userEmail)
                .orElseThrow(() -> new InvalidUserException(BaseResponseStatus.USER_NOT_FOUND));
        assertNull(restoredUser.getDeletedAt());
    }

    @Test
    @DisplayName("닉네임 수정 - 사용 가능")
    public void testUpdateNickName_Available() {
        // Given
        Long userId = 1L;
        String oldNickname = "oldNickName";
        String newNickname = "uniqueNickName";
        NicknameRequestDTO requestDto = new NicknameRequestDTO(newNickname);

        User user = userRepository.save(User.builder()
            .userEmail("testuser@example.com")
            .nickName(oldNickname)
            .password("password123")
            .profileImageUrlSmall("https://example.com/default-small.png")
            .profileImageUrlLarge("https://example.com/default-large.png")
            .role(Role.ROLE_USER)
            .build());

        // When & Then (예외가 발생하지 않아야 함)
        assertDoesNotThrow(() -> myPageService.updateNickName(user.getId(), requestDto));
    }

    @Test
    @DisplayName("닉네임 수정 - 중복 닉네임 존재")
    public void testUpdateNickName_Duplicate() {
        Long userId = 1L;
        String nickname = "existingNickName";
        NicknameRequestDTO requestDto = new NicknameRequestDTO(nickname);

        // Given: 중복 닉네임을 데이터베이스에 저장
        userRepository.save(User.builder()
            .userEmail("existinguser@example.com")
            .nickName(nickname)
            .password("password123")
            .profileImageUrlSmall("https://example.com/default-small.png")
            .profileImageUrlLarge("https://example.com/default-large.png")
            .role(Role.ROLE_USER)  // 추가
            .build());

        // When & Then (예외가 발생해야 함)
        InvalidUserException exception = assertThrows(
            InvalidUserException.class,
            () -> myPageService.updateNickName(userId, requestDto)
        );

        assertEquals(BaseResponseStatus.USER_NICKNAME_DUPLICATE, exception.getStatus());
    }
}