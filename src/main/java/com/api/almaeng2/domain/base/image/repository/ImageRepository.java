package com.api.almaeng2.domain.base.image.repository;

import com.api.almaeng2.domain.base.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
