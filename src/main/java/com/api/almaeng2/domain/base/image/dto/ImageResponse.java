package com.api.almaeng2.domain.base.image.dto;

import com.api.almaeng2.domain.base.image.entity.Image;
import lombok.Getter;

@Getter
public class ImageResponse {

    private final Long imageId;
    private final String imageUrl;

    public ImageResponse(Image image) {
        imageId = image.getId();
        imageUrl = image.getImageUrl();
    }
}
