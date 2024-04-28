package com.api.almaeng2.domain.base.image.converter;

import com.api.almaeng2.domain.base.image.dto.ImageResponse;
import com.api.almaeng2.domain.base.image.entity.Image;

import java.util.List;

public class ImageConverter {

    public static List<ImageResponse> imageToImageResponse(List<Image> images){
        return images.stream()
                .map(ImageResponse::new)
                .toList();
    }
}
