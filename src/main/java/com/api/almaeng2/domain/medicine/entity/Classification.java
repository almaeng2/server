package com.api.almaeng2.domain.medicine.entity;

import com.api.almaeng2.domain.household.entity.HouseholdPill;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "CLASSIFICATION_NAME")
public class Classification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String className;

    @OneToMany(mappedBy = "classification")
    private List<Pill> pills = new ArrayList<>();

    @OneToMany(mappedBy = "classification")
    private List<HouseholdPill> householdPills = new ArrayList<>();
}
