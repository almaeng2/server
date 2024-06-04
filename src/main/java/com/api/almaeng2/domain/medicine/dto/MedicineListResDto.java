package com.api.almaeng2.domain.medicine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class MedicineListResDto {

    @Schema(description = "알약명")
    private String name;

    @Schema(description = "성상")
    private String shape;

    @Schema(description = "복용 조건")
    private String effect;

    @Schema(description = "유의 사항")
    private String caution;

    @Schema(description = "이미지")
    private String imageUrl;

    @Schema(description = "분류")
    private List<String> classification = new ArrayList<>();
}
