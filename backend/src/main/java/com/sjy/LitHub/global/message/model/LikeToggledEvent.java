package com.sjy.LitHub.global.message.model;

public record LikeToggledEvent(Long postId, Long userId, boolean liked) {}
