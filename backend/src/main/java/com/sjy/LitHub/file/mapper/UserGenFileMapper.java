package com.sjy.LitHub.file.mapper;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.file.ImageType;
import com.sjy.LitHub.file.entity.UserGenFile;
import com.sjy.LitHub.file.util.local.LocalFileUtil;
import com.sjy.LitHub.file.util.common.ProfileImageUtil;
import com.sjy.LitHub.global.config.AppConfig;
import com.sjy.LitHub.global.exception.custom.InvalidFileException;
import com.sjy.LitHub.global.model.BaseResponseStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserGenFileMapper {

	private final ProfileImageUtil profileImageUtil;

	public UserGenFile toEntity(User user, int size, String fileExt) {
		return UserGenFile.builder()
			.user(user)
			.typeCode(UserGenFile.TypeCode.fromSize(size))
			.fileNo(size)
			.fileExt(fileExt)
			.build();
	}

	public Map<UserGenFile.TypeCode, UserGenFile> toDefaultUserGenFiles(User user) {
		return ImageType.PROFILE.getSizes().stream()
			.map(size -> createDefault(user, size))
			.collect(Collectors.toMap(UserGenFile::getTypeCode, Function.identity()));
	}

	private UserGenFile createDefault(User user, int size) {
		String pathOrKey;

		if (AppConfig.isProd()) {
			pathOrKey = profileImageUtil.copyBaseProfileOnS3(user.getId(), size);
		} else {
			pathOrKey = ProfileImageUtil.copyBaseProfileLocal(user.getId(), size);
		}

		String fileExt = LocalFileUtil.getFileExtension(pathOrKey);

		UserGenFile file = UserGenFile.builder()
			.user(user)
			.typeCode(getTypeCodeBySize(size))
			.fileNo(size)
			.fileExt(fileExt)
			.build();

		file.initStorageKey(user.getId(), size);
		return file;
	}

	private UserGenFile.TypeCode getTypeCodeBySize(int size) {
		return Arrays.stream(UserGenFile.TypeCode.values())
			.filter(tc -> tc.name().endsWith(String.valueOf(size)))
			.findFirst()
			.orElseThrow(() -> new InvalidFileException(BaseResponseStatus.INVALID_FILE_TYPE));
	}
}