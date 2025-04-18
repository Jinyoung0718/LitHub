package com.sjy.LitHub.post.model.res.toggle;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScrapResponseDTO {
	private boolean scrapped;
	private long scrapCount;
}