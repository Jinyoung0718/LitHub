package com.sjy.LitHub.post.cache.interaction;


public record PostInteractionState(boolean liked, long likeCount, boolean scrapped, long scrapCount) {
} // 좋아요/스크랩 여부 및 수치를 통합한 DTO 역할 (Redis 에 JSON 으로 직렬화되어 저장됨)