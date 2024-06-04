package com.api.almaeng2.domain.medicine.repository;

import com.api.almaeng2.domain.medicine.entity.Pill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PillRepository extends JpaRepository<Pill, Long> {

    @Query("select c.id from Pill as p join p.prescription as r join p.classification as c where r.id=:prescriptionId")
    Optional<List<Long>> findAllByPrescriptionId(@Param("prescriptionId") Long prescriptionId);

}
