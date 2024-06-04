package com.api.almaeng2.domain.household.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HouseholdResDto {

    @Schema(description = "안전상비약품명")
    private String name;

    @Schema(description = "겉모양")
    private String shape;

    @Schema(description = "복용 조건")
    private String effect;

    @Schema(description = "유의 사항")
    private String caution;

    @Schema(description = "이미지")
    private String imageUrl;

    @Schema(description = "복용 효과")
    private List<String> classification = new ArrayList<>();
}
