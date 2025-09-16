package com.sjy.LitHub.account.service.email;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import com.sjy.LitHub.global.exception.custom.InvalidUserException;

@ActiveProfiles("test")
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private MailProperties mailProperties;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        given(mailProperties.getUsername()).willReturn("test@example.com");
    }

    @Test
    @DisplayName("이미 인증된 이메일은 인증 코드 전송 시 예외 발생")
    void sendCode_alreadyVerified() {
        // given
        String email = "user@example.com";
        given(emailVerificationService.isRequestLocked(email)).willReturn(false);
        given(emailVerificationService.isAlreadyVerified(email)).willReturn(true);

        // when, then
        assertThatThrownBy(() -> emailService.sendEmailVerificationCode(email))
            .isInstanceOf(InvalidUserException.class);
    }

    @Test
    @DisplayName("인증 코드 검증 성공")
    void verifyCode_success() {
        // given
        String email = "user@example.com";
        String code = "123456";
        given(emailVerificationService.isAlreadyVerified(email)).willReturn(false);
        given(emailVerificationService.getStoredCode(email)).willReturn(code);

        // when
        emailService.verifyEmailCode(email, code);

        // then
        then(emailVerificationService).should().markAsVerified(email);
    }

    @Test
    @DisplayName("인증 코드 불일치 시 검증 실패")
    void verifyCode_invalid() {
        // given
        String email = "user@example.com";
        given(emailVerificationService.isAlreadyVerified(email)).willReturn(false);
        given(emailVerificationService.getStoredCode(email)).willReturn("654321");

        // when, then
        assertThatThrownBy(() -> emailService.verifyEmailCode(email, "123456"))
            .isInstanceOf(InvalidUserException.class);
    }
}