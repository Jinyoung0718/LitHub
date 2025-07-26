package com.sjy.LitHub.file.util;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StorageImageUtilTest {

	@Test
	@DisplayName("파일 사이즈 파싱 - KB")
	void parseMaxSize_kb() {
		long result = StorageImageUtil.parseMaxSize("5KB");
		assertThat(result).isEqualTo(5 * 1024L);
	}

	@Test
	@DisplayName("파일 사이즈 파싱 - MB")
	void parseMaxSize_mb() {
		long result = StorageImageUtil.parseMaxSize("2MB");
		assertThat(result).isEqualTo(2 * 1024L * 1024L);
	}

	@Test
	@DisplayName("파일 사이즈 파싱 - GB")
	void parseMaxSize_gb() {
		long result = StorageImageUtil.parseMaxSize("1GB");
		assertThat(result).isEqualTo(1024L * 1024L * 1024L);
	}

	@Test
	@DisplayName("파일 사이즈 파싱 - 잘못된 입력 시 기본값 반환")
	void parseMaxSize_invalid() {
		long result = StorageImageUtil.parseMaxSize("invalid");
		assertThat(result).isEqualTo(10 * 1024 * 1024L);
	}

	@Test
	@DisplayName("파일 사이즈 파싱 - 공백 입력 시 기본값 반환")
	void parseMaxSize_blank() {
		long result = StorageImageUtil.parseMaxSize("");
		assertThat(result).isEqualTo(10 * 1024 * 1024L);
	}

	@Test
	@DisplayName("파일 사이즈 파싱 - null 입력 시 기본값 반환")
	void parseMaxSize_null() {
		long result = StorageImageUtil.parseMaxSize(null);
		assertThat(result).isEqualTo(10 * 1024 * 1024L);
	}
}