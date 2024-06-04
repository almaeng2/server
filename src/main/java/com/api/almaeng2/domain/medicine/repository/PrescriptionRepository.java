package com.api.almaeng2.domain.medicine.repository;

import com.api.almaeng2.domain.medicine.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    @Query("select m from Prescription as m where m.id=:id")
    Optional<Prescription> findById(@Param("id") Long id);

    @Query("select m from Prescription as m where m.name=:name")
    Optional<Prescription> findByName(@Param("name") String name);
}
