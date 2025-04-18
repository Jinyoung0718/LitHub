package com.sjy.LitHub.post.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.post.model.res.PopularKeywordResponseDTO;
import com.sjy.LitHub.post.cache.PopularKeywordManager;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PopularKeywordService {

	private final PopularKeywordManager popularKeywordManager;

	@Transactional(readOnly = true)
	public List<PopularKeywordResponseDTO> getRealtimeTopKeywords() {
		return popularKeywordManager.getRealtimeTopKeywords().stream()
			.map(PopularKeywordResponseDTO::new)
			.toList();
	}

	@Transactional(readOnly = true)
	public List<PopularKeywordResponseDTO> getRealtimeTopTags() {
		return popularKeywordManager.getRealtimeTopTags().stream()
			.map(PopularKeywordResponseDTO::new)
			.toList();
	}
}