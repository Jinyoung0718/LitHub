package com.sjy.LitHub.file.storage.profile;

import java.util.Collection;

import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.file.entity.UserGenFile;

public interface ProfileImageStorage {

	void saveProfileImages(MultipartFile originalFile, Collection<UserGenFile> genFiles);

	void deleteProfileImages(User user);
}