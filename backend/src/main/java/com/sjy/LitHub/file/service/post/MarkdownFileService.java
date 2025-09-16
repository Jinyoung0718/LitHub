package com.sjy.LitHub.file.service.post;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.file.entity.PostGenFile;
import com.sjy.LitHub.file.mapper.PostGenFileMapper;
import com.sjy.LitHub.file.repository.post.PostGenFileRepository;
import com.sjy.LitHub.file.storage.post.PostImageStorage;
import com.sjy.LitHub.file.util.local.LocalFileUtil;
import com.sjy.LitHub.global.exception.custom.InvalidFileException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.global.util.AuthUser;
import com.sjy.LitHub.global.util.TransactionAfterCommitExecutor;
import com.sjy.LitHub.post.entity.Post;
import com.sjy.LitHub.post.model.req.UploadImageResponseDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarkdownFileService {

	private final PostImageStorage postImageStorage;
	private final PostGenFileMapper postGenFileMapper;
	private final PostGenFileRepository postGenFileRepository;
	private final TransactionAfterCommitExecutor txExecutor;

	@Transactional
	public UploadImageResponseDTO uploadTempMarkdownImage(MultipartFile file) {
		String fileExt = LocalFileUtil.getFileExtension(file.getOriginalFilename()).toLowerCase();
		PostGenFile image = postGenFileMapper.create(
			AuthUser.getAuthUser(),
			PostGenFile.TypeCode.MARKDOWN,
			fileExt
		);

		postImageStorage.savePostImage(file, image);
		image.initStorageKey(image.getOwnerModelId(), image.getFileNo());
		postGenFileRepository.save(image);

		txExecutor.executeAfterRollback(() -> {
			try {
				postImageStorage.deletePostImage(image);
			} catch (Exception e) {
				log.warn("롤백 중 마크다운 이미지 삭제 실패: {}", image.getStorageKey(), e);
			}
		});

		return UploadImageResponseDTO.from(image);
	}

	@Transactional
	public void deleteTempMarkdownImage(String storageKey) {
		PostGenFile file = postGenFileRepository
			.findByStorageKeyAndUserIdAndPostIsNull(storageKey, AuthUser.getUserId())
			.orElseThrow(() -> new InvalidFileException(BaseResponseStatus.IMAGE_DELETE_FAILED));

		postImageStorage.deletePostImage(file);
		postGenFileRepository.delete(file);

		txExecutor.executeAfterRollback(() -> {
			try {
				postGenFileRepository.save(file);
				postImageStorage.savePostImage(null, file);
			} catch (Exception e) {
				log.warn("롤백 중 마크다운 이미지 복구 실패: {}", file.getStorageKey(), e);
			}
		});
	}

	@Transactional
	public void syncMarkdownImages(Post post, String markdownContent) {
		List<String> usedFileNames = extractImageFileNames(markdownContent);
		cleanupUnusedImages(post, usedFileNames);
		if (usedFileNames.isEmpty()) return;

		List<PostGenFile> ordered = postGenFileRepository.findMarkdownImagesOrdered(
			post.getUser().getId(),
			usedFileNames
		);

		attachImagesInOrder(post, ordered);
	}

	private List<String> extractImageFileNames(String markdown) {
		Pattern pattern = Pattern.compile("!\\[.*?]\\(/gen/.*?/(.*?)\\)");
		Matcher matcher = pattern.matcher(markdown);
		List<String> fileNames = new ArrayList<>();
		while (matcher.find()) {
			fileNames.add(matcher.group(1));
		}
		return fileNames;
	}

	private void cleanupUnusedImages(Post post, List<String> usedFileNames) {
		List<PostGenFile> toDelete = postGenFileRepository.findMarkdownImagesToDelete(post.getId(), usedFileNames);
		postGenFileRepository.deleteAllInBatch(toDelete);
		toDelete.forEach(postImageStorage::deletePostImage);
	}

	private void attachImagesInOrder(Post post, List<PostGenFile> ordered) {
		IntStream.range(0, ordered.size()).forEach(i -> {
			int newNo = i + 1;
			PostGenFile img = ordered.get(i);

			if (img.getFileNo() != newNo) {
				postGenFileMapper.update(img, newNo, img.getFileExt());
				img.initStorageKey(img.getOwnerModelId(), img.getFileNo());
			}
			post.addImage(img);
		});
	}
}