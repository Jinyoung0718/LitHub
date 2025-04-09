package com.sjy.LitHub.post.model.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScrapResponseDTO {
	private boolean scrapped; // 현재 스크랩 상태
	private long scrapCount;
}