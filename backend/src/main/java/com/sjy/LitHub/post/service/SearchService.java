package com.sjy.LitHub.post.service;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.global.model.PageResponse;
import com.sjy.LitHub.post.cache.search.PopularSearchRecorder;
import com.sjy.LitHub.post.cache.search.SearchResultCacheStore;
import com.sjy.LitHub.post.cache.enums.CachePolicy;
import com.sjy.LitHub.post.mapper.PostMapper;
import com.sjy.LitHub.post.model.res.post.PostSummaryResponseDTO;
import com.sjy.LitHub.post.repository.post.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchService {

	private final PostRepository postRepository;
	private final SearchResultCacheStore searchResultCacheStore;
	private final PopularSearchRecorder popularSearchRecorder;
	private final PostMapper postMapper;

	private static final int PAGE_SIZE = 15;

	@Transactional(readOnly = true)
	public PageResponse<PostSummaryResponseDTO> searchByTitle(String keyword, Pageable pageable) {
		popularSearchRecorder.onSearch(keyword);
		return searchCommon(
			CachePolicy.SEARCH_BY_TITLE,
			() -> postRepository.searchByTitle(keyword, PageRequest.of(pageable.getPageNumber(), PAGE_SIZE)),
			keyword, pageable.getPageNumber()
		);
	} // 제목으로 검색 - (인기 제목 검색어에 한해 캐싱)

	@Transactional(readOnly = true)
	public PageResponse<PostSummaryResponseDTO> searchByTag(String tagName, Pageable pageable) {
		popularSearchRecorder.onTagSearch(tagName);
		return searchCommon(
			CachePolicy.SEARCH_BY_TAG,
			() -> postRepository.searchByTag(tagName, PageRequest.of(pageable.getPageNumber(), PAGE_SIZE)),
			tagName, pageable.getPageNumber()
		);
	} // 태그로 검색 - (인기 태그 검색어에 한해 캐싱)

	@Transactional(readOnly = true)
	public PageResponse<PostSummaryResponseDTO> searchByContent(String keyword, Pageable pageable) {
		Page<PostSummaryResponseDTO> page = postRepository.searchByContent(
			keyword, PageRequest.of(pageable.getPageNumber(), PAGE_SIZE)
		);
		postMapper.enrichPostSummaries(page.getContent());
		return PageResponse.from(page);
	} // 내용으로 검색 - 비캐싱

	@Transactional(readOnly = true)
	public PageResponse<PostSummaryResponseDTO> searchByTitleOrContent(String keyword, Pageable pageable) {
		Page<PostSummaryResponseDTO> page = postRepository.searchByTitleOrContent(
			keyword, PageRequest.of(pageable.getPageNumber(), PAGE_SIZE)
		);
		postMapper.enrichPostSummaries(page.getContent());
		return PageResponse.from(page);
	} // 제목 + 내용으로 검색 - 비캐싱

	@Transactional(readOnly = true)
	public List<String> getPopularKeywordList() {
		return popularSearchRecorder.getRealtimeTopKeywords();
	} // 인기 제목 검색어 리스트

	@Transactional(readOnly = true)
	public List<String> getPopularTagList() {
		return popularSearchRecorder.getRealtimeTopTags();
	} // 인기 태그 검색어 리스트

	private PageResponse<PostSummaryResponseDTO> searchCommon(
		CachePolicy policy,
		Supplier<Page<PostSummaryResponseDTO>> searchSupplier,
		String keywordOrTag,
		int pageNumber
	) {
		Page<PostSummaryResponseDTO> page = searchResultCacheStore.getOrPut(
			policy,
			searchSupplier,
			keywordOrTag,
			pageNumber
		);
		postMapper.enrichPostSummaries(page.getContent());
		return PageResponse.from(page);
	}
}