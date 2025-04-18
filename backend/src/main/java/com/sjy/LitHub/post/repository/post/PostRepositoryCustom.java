package com.sjy.LitHub.post.repository.post;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.sjy.LitHub.post.model.res.post.PostDetailResponseDTO;

public interface PostRepositoryCustom {
	Optional<PostDetailResponseDTO> findDetailDtoById(Long postId, Long currentUserId);

	List<Long> findTopPopularPostIds(Pageable pageable);
}