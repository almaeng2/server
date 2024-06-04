package com.api.almaeng2.domain.household.repository;

import com.api.almaeng2.domain.household.entity.HouseholdMedicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HouseholdMedicineRepository extends JpaRepository<HouseholdMedicine, Long> {

    @Query("select m from HouseholdMedicine as m where m.name=:name")
    Optional<HouseholdMedicine> findByName(String name);

}
