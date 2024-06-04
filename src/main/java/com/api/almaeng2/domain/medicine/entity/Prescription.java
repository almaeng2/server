package com.api.almaeng2.domain.medicine.entity;

import com.api.almaeng2.domain.base.image.entity.Image;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prescriptionId")
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "prescription")
    private List<Pill> pills = new ArrayList<>();

    @Column(nullable = false)
    private String shape;

    @Column(nullable = false)
    private String effect;

    @Column(nullable = false)
    private String caution;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imageId")
    private Image image;

    @OneToMany(mappedBy = "prescription")
    private List<PrescriptionsList> prescriptionsLists = new ArrayList<>();
}
