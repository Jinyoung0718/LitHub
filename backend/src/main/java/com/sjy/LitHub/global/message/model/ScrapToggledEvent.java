package com.sjy.LitHub.global.message.model;

public record ScrapToggledEvent(Long postId, Long userId, boolean scrapped) {}