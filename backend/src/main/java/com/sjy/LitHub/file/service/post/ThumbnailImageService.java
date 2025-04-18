package com.sjy.LitHub.file.service.post;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.file.entity.PostGenFile;
import com.sjy.LitHub.file.mapper.PostGenFileMapper;
import com.sjy.LitHub.file.repository.post.PostGenFileRepository;
import com.sjy.LitHub.file.storage.post.PostImageStorage;
import com.sjy.LitHub.global.AuthUser;
import com.sjy.LitHub.global.exception.custom.InvalidFileException;
import com.sjy.LitHub.global.model.BaseResponseStatus;
import com.sjy.LitHub.post.entity.Post;
import com.sjy.LitHub.post.model.req.UploadImageResponseDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThumbnailImageService {

	private final PostImageStorage postImageStorage;
	private final PostGenFileMapper postGenFileMapper;
	private final PostGenFileRepository postGenFileRepository;

	@Transactional
	public UploadImageResponseDTO uploadTempThumbnailImage(MultipartFile file) {
		User user = AuthUser.getAuthUser();
		PostGenFile image = postGenFileMapper.toEntity(user, file, PostGenFile.TypeCode.THUMBNAIL);
		postImageStorage.savePostImage(file, image);
		postGenFileRepository.save(image);
		return new UploadImageResponseDTO(image.getPublicUrl(), image.getFileName());
	}

	@Transactional
	public void assignThumbnailToPost(Post post, String thumbnailFileName, Long userId) {
		PostGenFile thumbnail = postGenFileRepository
			.findByFileNameAndUserIdAndTypeCode(thumbnailFileName, userId, PostGenFile.TypeCode.THUMBNAIL)
			.orElseThrow(() -> new InvalidFileException(BaseResponseStatus.IMAGE_PROCESSING_FAILED));

		post.addImage(thumbnail);
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
}