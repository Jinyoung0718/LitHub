package com.sjy.LitHub.file.mapper;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.file.ImageType;
import com.sjy.LitHub.file.entity.UserGenFile;
import com.sjy.LitHub.file.util.ProfileImageUtil;
import com.sjy.LitHub.file.util.FileUtil;
import com.sjy.LitHub.global.exception.custom.InvalidFileException;
import com.sjy.LitHub.global.model.BaseResponseStatus;

@Component
public class UserGenFileMapper {

	public Map<UserGenFile.TypeCode, UserGenFile> toUserGenFiles(User user, MultipartFile originalFile) {
		return ImageType.PROFILE.getSizes().stream()
			.map(size -> createUserGenFile(user, size, originalFile))
			.collect(Collectors.toMap(UserGenFile::getTypeCode, Function.identity()));
	}

	private UserGenFile.TypeCode getTypeCodeBySize(int size) {
		return Arrays.stream(UserGenFile.TypeCode.values())
			.filter(tc -> tc.name().endsWith(String.valueOf(size)))
			.findFirst()
			.orElseThrow(() -> new InvalidFileException(BaseResponseStatus.INVALID_FILE_TYPE));
	}

	private UserGenFile createUserGenFile(User user, int size, MultipartFile originalFile) {
		UserGenFile.TypeCode typeCode = getTypeCodeBySize(size);
		String originalFileName = originalFile.getOriginalFilename();
		String fileExt = FileUtil.getFileExtension(originalFileName);
		String fileExtTypeCode = FileUtil.getFileExtTypeCode(fileExt);
		String fileName = size + ".webp";

		return UserGenFile.builder()
			.user(user)
			.typeCode(typeCode)
			.fileNo(size)
			.originalFileName(originalFileName)
			.fileName(fileName)
			.fileExt(fileExt)
			.fileExtTypeCode(fileExtTypeCode)
			.directoryPath(String.valueOf(user.getId()))
			.build();
	}

	public Map<UserGenFile.TypeCode, UserGenFile> toDefaultUserGenFiles(User user) {
		return ImageType.PROFILE.getSizes().stream()
			.map(size -> createDefault(user, size))
			.collect(Collectors.toMap(UserGenFile::getTypeCode, Function.identity()));
	}

	private UserGenFile createDefault(User user, int size) {
		String filePath = ProfileImageUtil.copyBaseProfileToUserDir(user.getId(), size);
		long fileSize = FileUtil.getFileSize(filePath);

		return UserGenFile.builder()
			.user(user)
			.typeCode(getTypeCodeBySize(size))
			.fileNo(size)
			.originalFileName("base-profile_" + size + ".webp")
			.fileName(size + ".webp")
			.fileExt("webp")
			.fileExtTypeCode("img")
			.directoryPath(String.valueOf(user.getId()))
			.fileSize(fileSize)
			.build();
	}
}