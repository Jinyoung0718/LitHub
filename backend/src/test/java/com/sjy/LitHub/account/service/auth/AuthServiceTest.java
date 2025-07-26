package com.sjy.LitHub.account.service.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.account.entity.authenum.Role;
import com.sjy.LitHub.account.mapper.UserMapper;
import com.sjy.LitHub.account.model.req.signup.SignupDTO;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.account.util.PasswordManager;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.global.redis.RedisService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock private UserRepository userRepository;
    @Mock private RedisService redisService;
    @Mock private PasswordManager passwordManager;
    @Mock private UserMapper userMapper;
    @Mock private BCryptPasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 성공 - 모든 조건을 만족")
    void signup_success() {
        SignupDTO dto = new SignupDTO("testNick", "test@example.com", "validPass1");
        when(redisService.getData("emailAuth:test@example.com:verified")).thenReturn("true");
        when(passwordManager.isInvalid("validPass1")).thenReturn(false);
        when(userRepository.existsByNickName("testNick")).thenReturn(false);
        when(userRepository.findByUserEmailAll("test@example.com")).thenReturn(Optional.empty());

        User mockUser = User.builder()
            .userEmail("test@example.com")
            .nickName("testNick")
            .password("validPass1")
            .role(Role.ROLE_USER)
            .build();
        when(userMapper.ofSignupDTO(dto)).thenReturn(mockUser);

        when(passwordEncoder.encode("validPass1")).thenReturn("encoded-password");
        authService.signup(dto);

        assertEquals("encoded-password", mockUser.getPassword());
        verify(userRepository).save(mockUser);
        verify(redisService).deleteData("emailAuth:test@example.com:verified");
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 인증 안 됨")
    void signup_fail_email_not_verified() {
        SignupDTO dto = new SignupDTO("nick", "unverified@example.com", "validPass1");

        when(redisService.getData("emailAuth:unverified@example.com:verified")).thenReturn(null);

        InvalidUserException ex = assertThrows(InvalidUserException.class, () -> authService.signup(dto));
        assertEquals(BaseResponseStatus.EMAIL_VERIFICATION_REQUIRED, ex.getStatus());
    }

    @Test
    @DisplayName("회원가입 실패 - 비밀번호 불충분")
    void signup_fail_password_invalid() {
        SignupDTO dto = new SignupDTO("nick", "test@example.com", "weak");

        when(redisService.getData("emailAuth:test@example.com:verified")).thenReturn("true");
        when(passwordManager.isInvalid("weak")).thenReturn(true);

        InvalidUserException ex = assertThrows(InvalidUserException.class, () -> authService.signup(dto));
        assertEquals(BaseResponseStatus.USER_PASSWORD_INVALID, ex.getStatus());
    }

    @Test
    @DisplayName("회원가입 실패 - 닉네임 중복")
    void signup_fail_nickname_duplicate() {
        SignupDTO dto = new SignupDTO("dupeNick", "nick@example.com", "validPass1");

        when(redisService.getData("emailAuth:nick@example.com:verified")).thenReturn("true");
        when(passwordManager.isInvalid("validPass1")).thenReturn(false);
        when(userRepository.existsByNickName("dupeNick")).thenReturn(true);

        InvalidUserException ex = assertThrows(InvalidUserException.class, () -> authService.signup(dto));
        assertEquals(BaseResponseStatus.USER_NICKNAME_DUPLICATE, ex.getStatus());
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 사용자")
    void signup_fail_email_exists() {
        SignupDTO dto = new SignupDTO("nick", "exists@example.com", "validPass1");

        when(redisService.getData("emailAuth:exists@example.com:verified")).thenReturn("true");
        when(passwordManager.isInvalid("validPass1")).thenReturn(false);
        when(userRepository.existsByNickName("nick")).thenReturn(false);

        User existingUser = User.builder()
            .userEmail("exists@example.com")
            .nickName("existingNick")
            .password("hashed-password")
            .build();

        when(userRepository.findByUserEmailAll("exists@example.com")).thenReturn(Optional.of(existingUser));

        InvalidUserException ex = assertThrows(InvalidUserException.class, () -> authService.signup(dto));
        assertEquals(BaseResponseStatus.USER_ALREADY_EXISTS, ex.getStatus());
    }


    @Test
    @DisplayName("회원가입 실패 - 복구 대상 계정 존재")
    void signup_fail_deleted_user_exists() {
        SignupDTO dto = new SignupDTO("nick", "deleted@example.com", "validPass1");

        User deletedUser = User.builder()
            .userEmail("deleted@example.com")
            .nickName("nick")
            .password("xxx")
            .deletedAt(LocalDateTime.now())
            .build();

        when(redisService.getData("emailAuth:deleted@example.com:verified")).thenReturn("true");
        when(passwordManager.isInvalid("validPass1")).thenReturn(false);
        when(userRepository.existsByNickName("nick")).thenReturn(false);
        when(userRepository.findByUserEmailAll("deleted@example.com")).thenReturn(Optional.of(deletedUser));

        InvalidUserException ex = assertThrows(InvalidUserException.class, () -> authService.signup(dto));
        assertEquals(BaseResponseStatus.USER_ALREADY_EXISTS, ex.getStatus());
    }

    @Test
    @DisplayName("회원가입 실패 - 비밀번호 암호화 실패")
    void signup_fail_password_encoding_failure() {
        SignupDTO dto = new SignupDTO("testNick", "test@example.com", "validPass1");

        when(redisService.getData("emailAuth:test@example.com:verified")).thenReturn("true");
        when(passwordManager.isInvalid("validPass1")).thenReturn(false);
        when(userRepository.existsByNickName("testNick")).thenReturn(false);
        when(userRepository.findByUserEmailAll("test@example.com")).thenReturn(Optional.empty());

        User mockUser = User.builder()
            .userEmail("test@example.com")
            .nickName("testNick")
            .password("validPass1")
            .role(Role.ROLE_USER)
            .build();

        when(userMapper.ofSignupDTO(dto)).thenReturn(mockUser);
        when(passwordEncoder.encode("validPass1")).thenThrow(new RuntimeException("인코딩 실패"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.signup(dto));
        assertEquals("인코딩 실패", exception.getMessage());
    }

    @Test
    @DisplayName("회원 복구 성공 - soft delete 복구")
    void restore_user_success() {
        String email = "restore@example.com";
        authService.restoreUser(email);
        verify(userRepository).restoreUserByEmail(email);
    }
}