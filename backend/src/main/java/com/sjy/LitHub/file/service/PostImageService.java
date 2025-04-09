package com.sjy.LitHub.file.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.file.entity.PostGenFile;
import com.sjy.LitHub.file.mapper.PostGenFileMapper;
import com.sjy.LitHub.file.repository.PostGenFileRepository;
import com.sjy.LitHub.file.storage.post.PostImageStorage;
import com.sjy.LitHub.global.AuthUser;
import com.sjy.LitHub.global.exception.custom.InvalidFileException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.post.entity.Post;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostImageService {

	private final PostImageStorage postImageStorage;
	private final PostGenFileMapper postGenFileMapper;
	private final PostGenFileRepository postGenFileRepository;

	@Transactional
	public String uploadTempMarkdownImage(MultipartFile file) {
		User user = AuthUser.getAuthUser();
		PostGenFile image = postGenFileMapper.toEntity(user, file, PostGenFile.TypeCode.MARKDOWN);

		postImageStorage.savePostImage(file, image);
		postGenFileRepository.save(image);
		return image.getPublicUrl();
	} // 마크다운 에디터에 삽입

	@Transactional
	public void deleteTempMarkdownImage(String fileName) {
		PostGenFile file = postGenFileRepository.findByFileNameAndUserIdAndPostIsNull(fileName, AuthUser.getUserId())
			.orElseThrow(() -> new InvalidFileException(BaseResponseStatus.IMAGE_DELETE_FAILED));

		postImageStorage.deletePostImage(file);
		postGenFileRepository.delete(file);
	}

	@Transactional
	public void assignImagesToPost(Post post, MultipartFile thumbnail, Long userId) {
		PostGenFile thumbnailImage = postGenFileMapper.toEntity(post.getUser(), thumbnail, PostGenFile.TypeCode.THUMBNAIL);
		postImageStorage.savePostImage(thumbnail, thumbnailImage);
		post.addImage(thumbnailImage);

		List<PostGenFile> images = postGenFileRepository.findTemporaryMarkdownImagesByUser(userId);
		images.forEach(post::addImage);
	}

	@Transactional
	public void updateThumbnail(Post post, MultipartFile newThumbnail) {
		PostGenFile existing = post.getImages().get(PostGenFile.TypeCode.THUMBNAIL);

		if (existing != null) {
			postImageStorage.savePostImage(newThumbnail, existing);
			postGenFileMapper.updateMetadata(existing, newThumbnail);
		} else {
			PostGenFile newThumb = postGenFileMapper.toEntity(post.getUser(), newThumbnail, PostGenFile.TypeCode.THUMBNAIL);
			postImageStorage.savePostImage(newThumbnail, newThumb);
			post.addImage(newThumb);
		}
	}

	@Transactional
	public void syncMarkdownImages(Post post, String markdownContent) {
		List<String> usedFileNames = extractImageFileNames(markdownContent);
		Set<String> usedSet = new HashSet<>(usedFileNames);

		List<PostGenFile> currentImages = postGenFileRepository
			.findByPostIdAndTypeCode(post.getId(), PostGenFile.TypeCode.MARKDOWN);

		for (PostGenFile image : currentImages) {
			String fileName = image.getFileName();

			if (usedSet.contains(fileName)) {
				usedSet.remove(fileName);
			} else {
				postImageStorage.deletePostImage(image);
				postGenFileRepository.delete(image);
			}
		}

		if (!usedSet.isEmpty()) {
			List<PostGenFile> newImages = postGenFileRepository
				.findAllByFileNameInAndUserIdAndTypeCode(new ArrayList<>(usedSet), post.getUser().getId(), PostGenFile.TypeCode.MARKDOWN);
			for (PostGenFile img : newImages) {
				post.addImage(img);
			}
		}
	}

	public List<String> extractImageFileNames(String markdown) {
		Pattern pattern = Pattern.compile("!\\[.*?]\\(/gen/.*?/(.*?)\\)");
		Matcher matcher = pattern.matcher(markdown);
		List<String> fileNames = new ArrayList<>();
		while (matcher.find()) {
			fileNames.add(matcher.group(1));
		}
		return fileNames;
	}
}