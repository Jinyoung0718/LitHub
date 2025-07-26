package com.sjy.LitHub.account.service.email;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import com.sjy.LitHub.global.redis.RedisService;

@ActiveProfiles("test")
class EmailVerificationServiceTest {

	@Mock
	private RedisService redisService;

	@InjectMocks
	private EmailVerificationService emailVerificationService;

	private static final String EMAIL = "test@example.com";

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("이메일 인증 요청 잠금 여부 확인")
	void isRequestLocked_shouldReturnTrue_whenLockedInRedis() {
		when(redisService.getData("emailAuth:" + EMAIL + ":requestLock")).thenReturn("true");

		boolean result = emailVerificationService.isRequestLocked(EMAIL);

		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("이메일 인증 완료 여부 확인")
	void isAlreadyVerified_shouldReturnTrue_whenVerifiedInRedis() {
		when(redisService.getData("emailAuth:" + EMAIL + ":verified")).thenReturn("true");

		boolean result = emailVerificationService.isAlreadyVerified(EMAIL);

		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("이메일 인증 코드 저장 시 관련 데이터 설정")
	void storeVerificationCode_shouldSetCodeAndLocks() {
		emailVerificationService.storeVerificationCode(EMAIL, "123456");

		verify(redisService).setData(eq("emailAuth:" + EMAIL + ":code"), eq("123456"), eq(600L));
		verify(redisService).setData(eq("emailAuth:" + EMAIL + ":verified"), eq("false"), eq(600L));
		verify(redisService).setData(eq("emailAuth:" + EMAIL + ":requestLock"), eq("true"), eq(30L)); // ← 수정됨
	}


	@Test
	@DisplayName("저장된 인증 코드 조회")
	void getStoredCode_shouldReturnCodeFromRedis() {
		when(redisService.getData("emailAuth:" + EMAIL + ":code")).thenReturn("123456");

		String code = emailVerificationService.getStoredCode(EMAIL);

		assertThat(code).isEqualTo("123456");
	}

	@Test
	@DisplayName("인증 완료 처리 시 verified 상태 true로 설정 및 코드 삭제")
	void markAsVerified_shouldUpdateStatusAndDeleteCode() {
		emailVerificationService.markAsVerified(EMAIL);

		verify(redisService).setData(eq("emailAuth:" + EMAIL + ":verified"), eq("true"), eq(600L));
		verify(redisService).deleteData(eq("emailAuth:" + EMAIL + ":code"));
	}

	@Test
	@DisplayName("인증 코드 삭제")
	void clearVerificationCode_shouldDeleteCodeFromRedis() {
		emailVerificationService.clearVerificationCode(EMAIL);

		verify(redisService).deleteData(eq("emailAuth:" + EMAIL + ":code"));
	}
}