package com.sjy.LitHub.file.service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.file.entity.PostGenFile;
import com.sjy.LitHub.file.repository.post.PostGenFileRepository;
import com.sjy.LitHub.file.storage.post.PostImageStorage;
import com.sjy.LitHub.global.util.TransactionAfterCommitExecutor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UnusedImageCleaner {

	private final PostGenFileRepository postGenFileRepository;
	private final PostImageStorage postImageStorage;
	private final TransactionAfterCommitExecutor afterCommitExecutor;

	@Scheduled(cron = "0 0 * * * *") // 매 정시 실행
	@Transactional
	public void deleteUnusedImages() {
		LocalDateTime threshold = LocalDateTime.now().minusHours(1);

		List<PostGenFile> allToDelete = Stream.of(
			postGenFileRepository.findUnusedImagesByTypeBefore(PostGenFile.TypeCode.MARKDOWN, threshold),
			postGenFileRepository.findUnusedImagesByTypeBefore(PostGenFile.TypeCode.THUMBNAIL, threshold)
		).flatMap(Collection::stream).toList();

		if (allToDelete.isEmpty()) {
			return;
		}

		postGenFileRepository.deleteAll(allToDelete);
		afterCommitExecutor.executeAfterCommit(() -> allToDelete.forEach(file -> {
			try {
				postImageStorage.deletePostImage(file);
			} catch (Exception e) {
				log.warn("이미지 파일 삭제 실패 | id: {}, storageKey: {}", file.getId(), file.getStorageKey(), e);
			}
		}));
	}
}