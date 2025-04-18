package com.sjy.LitHub.post.repository.post;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sjy.LitHub.post.model.res.post.PostSummaryResponseDTO;

public interface PostSortCustom {
	Page<PostSummaryResponseDTO> searchByKeyword(String keyword, Pageable pageable);

	Page<PostSummaryResponseDTO> searchByTag(String tagName, Pageable pageable);

	Page<PostSummaryResponseDTO> findPopularPosts(Pageable pageable);

	Page<PostSummaryResponseDTO> findMyPosts(Long userId, Pageable pageable);

	Page<PostSummaryResponseDTO> findPostsScrappedByUser(Long userId, Pageable pageable);

	Page<PostSummaryResponseDTO> findFollowerFeedsByPriority(List<Long> followeeIds, Pageable pageable);
}