package com.sjy.LitHub.post.repository.post;

import java.util.Optional;

import com.sjy.LitHub.post.model.res.post.PostDetailResponseDTO;

public interface PostRepositoryCustom {
	Optional<PostDetailResponseDTO> findDetailDtoById(Long postId, Long currentUserId);
}