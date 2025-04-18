package com.sjy.LitHub.file.repository.post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.sjy.LitHub.file.entity.PostGenFile;

public interface PostGenFileRepositoryCustom {
	List<PostGenFile> findUnusedImagesByTypeBefore(PostGenFile.TypeCode typeCode, LocalDateTime timeLimit);

	Optional<PostGenFile> findThumbnailByPostId(Long postId);

	List<PostGenFile> findMarkdownImagesByPostId(Long postId);

	List<PostGenFile> findThumbnailsByPostIds(List<Long> postIds);
}