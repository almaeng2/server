package com.api.almaeng2.domain.medicine.repository;

import com.api.almaeng2.domain.medicine.entity.PrescriptionList;
import com.api.almaeng2.domain.medicine.entity.PrescriptionsList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionsListRepository extends JpaRepository<PrescriptionsList, Long> {

    @Query("select b.id from PrescriptionsList as p join p.prescriptionList as a join p.prescription as b where a.id=:id")
    Optional<List<Long>> findAllByPrescriptionListId(@Param("id") Long id);

    @Modifying
    @Query("delete from PrescriptionsList as p where p.prescriptionList IN (select pa from PrescriptionList as pa where pa.id=:id)")
    void deleteAllByPrescriptionListId(@Param("id") Long id);

}
