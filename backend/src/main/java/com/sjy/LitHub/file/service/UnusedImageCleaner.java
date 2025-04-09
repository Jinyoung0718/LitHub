package com.sjy.LitHub.file.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.file.entity.PostGenFile;
import com.sjy.LitHub.file.repository.PostGenFileRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UnusedImageCleaner {

	private final PostGenFileRepository postGenFileRepository;

	@Scheduled(cron = "0 0 * * * *")
	@Transactional
	public void deleteUnusedMarkdownImages() {
		LocalDateTime threshold = LocalDateTime.now().minusHours(1);
		List<PostGenFile> unusedImages = postGenFileRepository.findUnusedMarkdownImagesBefore(threshold);
		postGenFileRepository.deleteAll(unusedImages);
	}
}