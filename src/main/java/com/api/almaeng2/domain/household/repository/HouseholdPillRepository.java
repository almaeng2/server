package com.api.almaeng2.domain.household.repository;

import com.api.almaeng2.domain.household.entity.HouseholdPill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HouseholdPillRepository extends JpaRepository<HouseholdPill, Long> {

    @Query("select c.id from HouseholdPill as h join h.householdMedicine as ho join h.classification as c where ho.id=:householdPillId")
    Optional<List<Long>> findAllByPrescriptionId(@Param("householdPillId") Long householdPillId);
}
