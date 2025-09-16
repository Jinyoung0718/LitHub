package com.sjy.LitHub.global.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageResponse<T> {
	private List<T> content;
	private int pageNumber;
	private int pageSize;
	private long totalElements;
	private int totalPages;
	private boolean last;

	public static <T> PageResponse<T> from(Page<T> page) {
		return new PageResponse<>(
			page.getContent(),
			page.getNumber(),
			page.getSize(),
			page.getTotalElements(),
			page.getTotalPages(),
			page.isLast()
		);
	}

	public static <T> PageResponse<T> from(List<T> content, Pageable pageable) {
		return new PageResponse<>(
			content,
			pageable.getPageNumber(),
			pageable.getPageSize(),
			content.size(),
			1,
			true
		);
	}

	public static <T> PageResponse<T> empty(Pageable pageable) {
		return new PageResponse<>(
			List.of(),
			pageable.getPageNumber(),
			pageable.getPageSize(),
			0,
			0,
			true);
	}
}