package com.sjy.LitHub.post.cache.enums;

public enum InteractionType {
	LIKE("likes"), SCRAP("scraps");

	private final String key;

	InteractionType(String key) {
		this.key = key;
	}
	public String key(Long postId) {
		return CachePolicy.POST_INTERACTION.createKey(key, postId);
	}
}