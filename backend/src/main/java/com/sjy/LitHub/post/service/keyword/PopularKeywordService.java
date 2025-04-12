package com.sjy.LitHub.post.service.keyword;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.post.model.res.PopularKeywordResponseDTO;

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
}