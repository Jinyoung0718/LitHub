package com.sjy.LitHub.file.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.file.entity.PostGenFile;
import com.sjy.LitHub.file.repository.post.PostGenFileRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UnusedImageCleaner {

	private final PostGenFileRepository postGenFileRepository;

	@Scheduled(cron = "0 0 * * * *") // 매 정시
	@Transactional
	public void deleteUnusedImages() {
		LocalDateTime threshold = LocalDateTime.now().minusHours(1);

		List<PostGenFile> unusedMarkdown = postGenFileRepository
			.findUnusedImagesByTypeBefore(PostGenFile.TypeCode.MARKDOWN, threshold);

		List<PostGenFile> unusedThumbnails = postGenFileRepository
			.findUnusedImagesByTypeBefore(PostGenFile.TypeCode.THUMBNAIL, threshold);

		List<PostGenFile> allToDelete = new ArrayList<>();
		allToDelete.addAll(unusedMarkdown);
		allToDelete.addAll(unusedThumbnails);

		postGenFileRepository.deleteAll(allToDelete);

		log.info("사용되지 않은 이미지 정리 완료 | 총: {}, 마크다운: {}, 썸네일: {}",
			allToDelete.size(), unusedMarkdown.size(), unusedThumbnails.size());
	}
}