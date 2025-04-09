package com.sjy.LitHub.file.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.sjy.LitHub.file.entity.PostGenFile;

public interface PostGenFileRepositoryCustom {
	List<PostGenFile> findUnusedMarkdownImagesBefore(LocalDateTime timeLimit);
	List<PostGenFile> findTemporaryMarkdownImagesByUser(Long userId);
}