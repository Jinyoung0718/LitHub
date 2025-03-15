package com.sjy.LitHub.account.service.email;

import com.sjy.LitHub.TestContainerConfig;
import com.sjy.LitHub.global.exception.custom.InvalidUserException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class EmailServiceTest extends TestContainerConfig {

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private RedisEmailService redisEmailService;

    private static final String TEST_EMAIL = "testuser@example.com";

    @AfterEach
    public void cleanUp() {
        redisEmailService.deleteData("emailAuth:" + TEST_EMAIL + ":code");
        redisEmailService.deleteData("emailAuth:" + TEST_EMAIL + ":verified");
        redisEmailService.deleteData("emailAuth:" + TEST_EMAIL + ":requestLock");
    }

    @Test
    @DisplayName("이메일 인증 코드 발송 - 정상 케이스")
    public void testSendEmailVerificationCode() {
        emailVerificationService.clearVerificationCode(TEST_EMAIL);
        emailService.sendEmailVerificationCode(TEST_EMAIL);

        String storedCode = redisEmailService.getData("emailAuth:" + TEST_EMAIL + ":code");
        assertNotNull(storedCode);
    }

    @Test
    @DisplayName("이메일 인증 코드 요청이 잠겨 있는 경우 예외 발생")
    public void testSendEmailVerificationCode_LockedRequest() {
        redisEmailService.setData("emailAuth:" + TEST_EMAIL + ":requestLock", "true", 30);

        InvalidUserException thrown = assertThrows(InvalidUserException.class, () -> emailService.sendEmailVerificationCode(TEST_EMAIL));
        assertEquals(BaseResponseStatus.EMAIL_REQUEST_LOCKED, thrown.getStatus());
    }

    @Test
    @DisplayName("이미 인증된 이메일에 대해 인증 코드 발송 시 예외 발생")
    public void testSendEmailVerificationCode_AlreadyVerified() {
        redisEmailService.setData("emailAuth:" + TEST_EMAIL + ":verified", "true", 600);

        InvalidUserException thrown = assertThrows(InvalidUserException.class, () -> emailService.sendEmailVerificationCode(TEST_EMAIL));
        assertEquals(BaseResponseStatus.EMAIL_ALREADY_VERIFIED, thrown.getStatus());
    }

    @Test
    @DisplayName("잘못된 인증 코드 입력 시 예외 발생")
    public void testVerifyEmailCode_InvalidCode() {
        String invalidCode = "654321";
        redisEmailService.setData("emailAuth:" + TEST_EMAIL + ":code", "123456", 600);

        InvalidUserException thrown = assertThrows(InvalidUserException.class, () -> emailService.verifyEmailCode(TEST_EMAIL, invalidCode));
        assertEquals(BaseResponseStatus.EMAIL_INVALID_CODE, thrown.getStatus());
    }

    @Test
    @DisplayName("이메일 인증 성공 후, 인증 코드 삭제 확인")
    public void testVerifyEmailCode_Success() {
        String validCode = "123456";
        redisEmailService.setData("emailAuth:" + TEST_EMAIL + ":code", validCode, 600);

        emailService.verifyEmailCode(TEST_EMAIL, validCode);

        assertEquals("true", redisEmailService.getData("emailAuth:" + TEST_EMAIL + ":verified"));
        assertNull(redisEmailService.getData("emailAuth:" + TEST_EMAIL + ":code"));
    }

    @Test
    @DisplayName("Redis TTL 설정 확인 - 인증 코드와 검증 상태의 유효기간 확인")
    public void testRedisTTL() throws InterruptedException {
        String validCode = "123456";
        redisEmailService.setData("emailAuth:" + TEST_EMAIL + ":code", validCode, 3); // 3초 후 만료

        Thread.sleep(4000); // 4초 대기 (유효기간 초과)

        String storedCode = redisEmailService.getData("emailAuth:" + TEST_EMAIL + ":code");
        assertNull(storedCode); // TTL 초과 후 데이터가 삭제되었는지 확인
    }

    @Test
    @DisplayName("동시 요청 처리 - 여러 개의 이메일 인증 요청이 올 경우")
    public void testConcurrentEmailVerificationRequests() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(5);

        for (int i = 0; i < 5; i++) {
            executorService.execute(() -> {
                try {
                    emailService.sendEmailVerificationCode(TEST_EMAIL);
                } catch (Exception e) {
                    System.out.println("Exception in concurrent test: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        String storedCode = redisEmailService.getData("emailAuth:" + TEST_EMAIL + ":code");
        assertNotNull(storedCode);
    }

    @Test
    @DisplayName("재시도 실패 후 recover() 메서드가 실행되는지 확인")
    public void testEmailRecoverLogic() {
        try {
            emailService.emailSendCode(TEST_EMAIL, "123456");
        } catch (Exception ignored) {
        }

        assertNull(redisEmailService.getData("emailAuth:" + TEST_EMAIL + ":code"));
    }
}