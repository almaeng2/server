package com.api.almaeng2.domain.medicine.repository;

import com.api.almaeng2.domain.medicine.dto.PrescriptionResDto;
import com.api.almaeng2.domain.medicine.entity.PrescriptionList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionListRepository extends JpaRepository<PrescriptionList, Long> {

    @Query("select p from PrescriptionList as p join p.member as m where m.userId=:userId")
    List<PrescriptionList> findAllByMemberId(@Param("userId") String userId);

    @Query("select p from PrescriptionList  as p join p.member as m where m.userId=:userId")
    Optional<PrescriptionList> findByMemberId(@Param("userId") String userId);
}
