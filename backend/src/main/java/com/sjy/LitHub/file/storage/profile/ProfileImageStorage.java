package com.sjy.LitHub.file.storage.profile;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.file.entity.UserGenFile;

public interface ProfileImageStorage {

	Map<UserGenFile.TypeCode, UserGenFile> saveProfileImagesAndReturnEntities(User user, MultipartFile originalFile);

	void deleteProfileImages(User user);
}