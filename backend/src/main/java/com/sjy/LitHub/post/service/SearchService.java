package com.sjy.LitHub.post.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.global.model.PageResponse;
import com.sjy.LitHub.post.cache.keyword.PopularKeywordManager;
import com.sjy.LitHub.post.cache.post.PostListCacheManager;
import com.sjy.LitHub.post.cache.enums.CachePolicy;
import com.sjy.LitHub.post.mapper.PostMapper;
import com.sjy.LitHub.post.model.res.post.PostSummaryResponseDTO;
import com.sjy.LitHub.post.repository.post.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchService {

	private final PostRepository postRepository;
	private final PostListCacheManager postListCacheManager;
	private final PopularKeywordManager popularKeywordManager;
	private final PostMapper postMapper;

	private static final int PAGE_SIZE = 15;

	@Transactional(readOnly = true)
	public PageResponse<PostSummaryResponseDTO> searchByTitle(String keyword, Pageable pageable) {
		popularKeywordManager.onSearch(keyword);
		Pageable limited = PageRequest.of(pageable.getPageNumber(), PAGE_SIZE);

		Page<PostSummaryResponseDTO> page = postListCacheManager.getOrPut(
			CachePolicy.SEARCH_BY_TITLE,
			() -> postRepository.searchByTitle(keyword, limited),
			keyword, pageable.getPageNumber()
		);

		postMapper.enrichPostSummaries(page.getContent());
		return PageResponse.from(page);
	}

	@Transactional(readOnly = true)
	public PageResponse<PostSummaryResponseDTO> searchByContent(String keyword, Pageable pageable) {
		popularKeywordManager.onSearch(keyword);
		Pageable limited = PageRequest.of(pageable.getPageNumber(), PAGE_SIZE);

		Page<PostSummaryResponseDTO> page = postRepository.searchByContent(keyword, limited);
		postMapper.enrichPostSummaries(page.getContent());
		return PageResponse.from(page);
	}

	@Transactional(readOnly = true)
	public PageResponse<PostSummaryResponseDTO> searchByTitleOrContent(String keyword, Pageable pageable) {
		popularKeywordManager.onSearch(keyword);
		Pageable limited = PageRequest.of(pageable.getPageNumber(), PAGE_SIZE);

		Page<PostSummaryResponseDTO> page = postRepository.searchByTitleOrContent(keyword, limited);
		postMapper.enrichPostSummaries(page.getContent());
		return PageResponse.from(page);
	}

	@Transactional(readOnly = true)
	public PageResponse<PostSummaryResponseDTO> searchByTag(String tagName, Pageable pageable) {
		popularKeywordManager.onTagSearch(tagName);
		Pageable limited = PageRequest.of(pageable.getPageNumber(), PAGE_SIZE);

		Page<PostSummaryResponseDTO> page = postListCacheManager.getOrPut(
			CachePolicy.SEARCH_BY_TAG,
			() -> postRepository.searchByTag(tagName, limited),
			tagName, pageable.getPageNumber()
		);

		postMapper.enrichPostSummaries(page.getContent());
		return PageResponse.from(page);
	}

	@Transactional(readOnly = true)
	public List<String> getPopularKeywordList() {
		return popularKeywordManager.getRealtimeTopKeywords();
	}

	@Transactional(readOnly = true)
	public List<String> getPopularTagList() {
		return popularKeywordManager.getRealtimeTopTags();
	}
}