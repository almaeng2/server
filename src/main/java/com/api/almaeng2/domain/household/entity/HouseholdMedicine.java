package com.api.almaeng2.domain.household.entity;

import com.api.almaeng2.domain.base.image.entity.Image;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class HouseholdMedicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String shape;
    private String effect;
    private String caution;

    @OneToOne
    private Image image;

    @OneToMany(mappedBy = "householdMedicine")
    private List<HouseholdPill> householdPills = new ArrayList<>();
}
