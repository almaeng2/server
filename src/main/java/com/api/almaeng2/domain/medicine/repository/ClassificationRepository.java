package com.api.almaeng2.domain.medicine.repository;

import com.api.almaeng2.domain.medicine.entity.Classification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassificationRepository extends JpaRepository<Classification, Long> {

    @Query("select c from CLASSIFICATION_NAME as c where c.id=:id")
    Optional<Classification> findByClassId(@Param("id") Long id);
}
