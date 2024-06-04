package com.api.almaeng2.domain.household.entity;

import com.api.almaeng2.domain.medicine.entity.Classification;
import com.api.almaeng2.domain.medicine.entity.Prescription;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HouseholdPill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HOUSEHOLDMEDICINE_ID")
    private HouseholdMedicine householdMedicine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLASSIFICATION_ID")
    private Classification classification;
}
