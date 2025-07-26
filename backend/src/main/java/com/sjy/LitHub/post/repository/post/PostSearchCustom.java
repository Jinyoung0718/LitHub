package com.sjy.LitHub.post.repository.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sjy.LitHub.post.model.res.post.PostSummaryResponseDTO;

public interface PostSearchCustom {
	Page<PostSummaryResponseDTO> searchByTitle(String keyword, Pageable pageable);

	Page<PostSummaryResponseDTO> searchByContent(String keyword, Pageable pageable);

	Page<PostSummaryResponseDTO> searchByTitleOrContent(String keyword, Pageable pageable);
}