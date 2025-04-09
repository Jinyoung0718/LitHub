package com.sjy.LitHub.file.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.file.entity.PostGenFile;
import com.sjy.LitHub.file.util.FileUtil;

@Component
public class PostGenFileMapper {

	public PostGenFile toEntity(User user, MultipartFile file, PostGenFile.TypeCode typeCode) {
		String originalFileName = file.getOriginalFilename();
		String fileExt = FileUtil.getFileExtension(originalFileName);
		String fileExtTypeCode = FileUtil.getFileExtTypeCode(fileExt);
		String fileName = UUID.randomUUID() + "." + fileExt;

		return PostGenFile.builder()
			.user(user)
			.typeCode(typeCode)
			.fileNo(0)
			.originalFileName(originalFileName)
			.fileName(fileName)
			.fileExt(fileExt)
			.fileExtTypeCode(fileExtTypeCode)
			.directoryPath(String.valueOf(user.getId()))
			.build();
	}

	public void updateMetadata(PostGenFile postGenFile, MultipartFile file) {
		String originalFileName = file.getOriginalFilename();
		String fileExt = FileUtil.getFileExtension(originalFileName);
		String fileExtTypeCode = FileUtil.getFileExtTypeCode(fileExt);

		postGenFile.setOriginalFileName(originalFileName);
		postGenFile.setFileExt(fileExt);
		postGenFile.setFileExtTypeCode(fileExtTypeCode);
		postGenFile.setFileSize(FileUtil.getFileSize(postGenFile.getFilePath()));
	}
}