package com.sjy.LitHub.account.service.email;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.BaseResponseStatus;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private EmailVerificationService emailVerificationService;

    private final String email = "test@example.com";

    @Test
    @DisplayName("이메일 인증 코드 전송 - 정상 케이스")
    void sendEmailVerificationCode_success() {
        // given
        when(emailVerificationService.isRequestLocked(email)).thenReturn(false);
        when(emailVerificationService.isAlreadyVerified(email)).thenReturn(false);

        // when
        emailService.sendEmailVerificationCode(email);

        // then
        verify(emailVerificationService).clearVerificationCode(email);
        verify(emailVerificationService).storeVerificationCode(eq(email), anyString());
        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("이메일 인증 코드 전송 - 이미 인증된 이메일")
    void sendEmailVerificationCode_alreadyVerified() {
        when(emailVerificationService.isRequestLocked(email)).thenReturn(false);
        when(emailVerificationService.isAlreadyVerified(email)).thenReturn(true);

        InvalidUserException ex = assertThrows(InvalidUserException.class,
            () -> emailService.sendEmailVerificationCode(email));

        assertEquals(BaseResponseStatus.EMAIL_ALREADY_VERIFIED, ex.getStatus());

        verify(emailVerificationService, never()).clearVerificationCode(email);
        verify(javaMailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("이메일 인증 코드 전송 - 요청이 잠겨 있음")
    void sendEmailVerificationCode_locked() {
        when(emailVerificationService.isRequestLocked(email)).thenReturn(true);

        InvalidUserException ex = assertThrows(InvalidUserException.class,
            () -> emailService.sendEmailVerificationCode(email));

        assertEquals(BaseResponseStatus.EMAIL_REQUEST_LOCKED, ex.getStatus());

        verify(javaMailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("이메일 인증 코드 검증 성공")
    void verifyEmailCode_success() {
        when(emailVerificationService.isAlreadyVerified(email)).thenReturn(false);
        when(emailVerificationService.getStoredCode(email)).thenReturn("123456");

        emailService.verifyEmailCode(email, "123456");

        verify(emailVerificationService).markAsVerified(email);
    }

    @Test
    @DisplayName("이메일 인증 코드 검증 실패 - 코드 불일치")
    void verifyEmailCode_invalidCode() {
        when(emailVerificationService.isAlreadyVerified(email)).thenReturn(false);
        when(emailVerificationService.getStoredCode(email)).thenReturn("123456");

        InvalidUserException ex = assertThrows(InvalidUserException.class,
            () -> emailService.verifyEmailCode(email, "999999"));

        assertEquals(BaseResponseStatus.EMAIL_INVALID_CODE, ex.getStatus());
    }
}