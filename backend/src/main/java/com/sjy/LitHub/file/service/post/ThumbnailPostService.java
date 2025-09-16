package com.sjy.LitHub.file.service.post;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.file.entity.PostGenFile;
import com.sjy.LitHub.file.mapper.PostGenFileMapper;
import com.sjy.LitHub.file.repository.post.PostGenFileRepository;
import com.sjy.LitHub.file.storage.post.PostImageStorage;
import com.sjy.LitHub.global.util.AuthUser;
import com.sjy.LitHub.global.exception.custom.InvalidFileException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.global.util.TransactionAfterCommitExecutor;
import com.sjy.LitHub.post.entity.Post;
import com.sjy.LitHub.post.model.req.UploadImageResponseDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThumbnailPostService {

	private final PostImageStorage postImageStorage;
	private final PostGenFileMapper postGenFileMapper;
	private final PostGenFileRepository postGenFileRepository;
	private final TransactionAfterCommitExecutor txExecutor;

	@Transactional
	public UploadImageResponseDTO uploadTempThumbnailImage(MultipartFile file) {
		PostGenFile image = postGenFileMapper.toEntity(AuthUser.getAuthUser(), file, PostGenFile.TypeCode.THUMBNAIL);
		postImageStorage.savePostImage(file, image);
		postGenFileRepository.save(image);

		txExecutor.executeAfterRollback(() -> {
			try {
				postImageStorage.deletePostImage(image);
			} catch (Exception e) {
				log.warn("롤백 중 업로드된 파일 정리 실패: {}", image.getStorageKey(), e);
			}
		});

		return new UploadImageResponseDTO(image.getPublicUrl(), image.getStorageKey());
	}

	@Transactional
	public void assignThumbnailToPost(Post post, String storageKey, Long userId) {
		PostGenFile thumbnail = postGenFileRepository
			.findByStorageKeyAndUserIdAndPostIsNull(storageKey, userId) // 아직 게시글에 연결되지 않은 임시 썸네일만 허용
			.orElseThrow(() -> new InvalidFileException(BaseResponseStatus.IMAGE_PROCESSING_FAILED));

		post.addImage(thumbnail);
	}

	@Transactional
	public void updateThumbnail(Post post, MultipartFile newThumbnail) {
		PostGenFile existing = post.getImages().get(PostGenFile.TypeCode.THUMBNAIL);

		if (existing != null) {
			postImageStorage.savePostImage(newThumbnail, existing);
		} else {
			PostGenFile newThumb = postGenFileMapper.toEntity(post.getUser(), newThumbnail, PostGenFile.TypeCode.THUMBNAIL);
			postImageStorage.savePostImage(newThumbnail, newThumb);
			post.addImage(newThumb);

			txExecutor.executeAfterRollback(() -> {
				try {
					postImageStorage.deletePostImage(newThumb);
				} catch (Exception e) {
					log.warn("롤백 중 썸네일 삭제 실패: {}", newThumb.getStorageKey(), e);
				}
			});
		}
	}
}