package com.api.almaeng2.domain.medicine.entity;

import com.api.almaeng2.domain.base.image.entity.Image;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class SafeProparation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "safe_preparation_id")
    private Long id;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "safe_preparation_id")
    private List<Image> images;
}
