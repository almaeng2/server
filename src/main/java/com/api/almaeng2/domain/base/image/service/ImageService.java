package com.api.almaeng2.domain.base.image.service;

import com.api.almaeng2.domain.base.image.entity.Image;
import com.api.almaeng2.domain.base.image.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageService {

    private final ImageRepository imageRepository;

    @Transactional
    public void register(final String imageUrl, final String imageName){
        Image image = Image.builder()
                .imageUrl(imageUrl)
                .imageName(imageName)
                .build();

        imageRepository.save(image);
    }
}
