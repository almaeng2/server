package com.api.almaeng2.domain.medicine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class PrescriptionResDto {

    @Schema(description = "리스트 아이디")
    private Long listId;

    @Schema(description = "처방전명")
    private String name;

    @Schema(description = "처방 시기")
    private String prescribedTime;
}
