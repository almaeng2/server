package com.api.almaeng2.domain.medicine.dto;

import com.api.almaeng2.domain.medicine.entity.Prescription;
import com.api.almaeng2.domain.medicine.entity.PrescriptionList;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PrescriptionListResDto {

    @Schema(description = "처방전 목록")
    private List<PrescriptionResDto> lists;
}
