package com.sjy.LitHub.file.storage.post;

import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.file.entity.PostGenFile;

public interface PostImageStorage {
	void savePostImage(MultipartFile file, PostGenFile postGenFile);
	void deletePostImage(PostGenFile postGenFile);
}