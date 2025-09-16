package com.sjy.LitHub.file.mapper;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.file.entity.PostGenFile;
import com.sjy.LitHub.file.util.local.LocalFileUtil;

@Component
public class PostGenFileMapper {

	public PostGenFile create(User user, PostGenFile.TypeCode typeCode, String fileExt) {
		return PostGenFile.builder()
			.user(user)
			.typeCode(typeCode)
			.fileNo(0)
			.fileExt(fileExt)
			.build();
	}

	public PostGenFile toEntity(User user, MultipartFile file, PostGenFile.TypeCode typeCode) {
		String fileExt = LocalFileUtil.getFileExtension(file.getOriginalFilename()).toLowerCase();
		return PostGenFile.builder()
			.user(user)
			.typeCode(typeCode)
			.fileNo(0)
			.fileExt(fileExt)
			.build();
	}

	public void update(PostGenFile postGenFile, int fileNo, String fileExt) {
		postGenFile.setFileNo(fileNo);
		postGenFile.setFileExt(fileExt);
	}
}