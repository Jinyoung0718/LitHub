package com.sjy.LitHub.file;

import lombok.Getter;

import java.util.List;

@Getter
public enum ImageType {
    PROFILE(List.of(256, 512)),
    POST_THUMBNAIL(List.of(600));

    private final List<Integer> sizes;

    ImageType(List<Integer> sizes) {
        this.sizes = sizes;
    }

    public int getPrimarySize() {
        return sizes.get(0);
    }
}