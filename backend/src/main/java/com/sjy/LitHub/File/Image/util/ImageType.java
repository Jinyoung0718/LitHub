package com.sjy.LitHub.File.Image.util;

import lombok.Getter;

import java.util.List;

@Getter
public enum ImageType {
    PROFILE(List.of(48, 128)),
    POST_THUMBNAIL(List.of(300)),
    POST_CONTENT(List.of(600));

    private final List<Integer> sizes;

    ImageType(List<Integer> sizes) {
        this.sizes = sizes;
    }
}