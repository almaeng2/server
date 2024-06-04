package com.api.almaeng2.domain.household.controller;

import com.api.almaeng2.domain.household.dto.HouseholdResDto;
import com.api.almaeng2.domain.household.service.HouseholdService;
import com.api.almaeng2.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/household")
public class HouseholdController {

    private final HouseholdService householdService;

    @Operation(summary = "안전상비약 정보 확인", description = "사용자가 촬영한 안전상비약을 분석하여 상세 정보를 조회합니다.")
    @PostMapping()
    public SuccessResponse<HouseholdResDto> getMedicine(@Valid @RequestPart(value = "image") MultipartFile image,
                                                        HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws IOException {

        HouseholdResDto result = householdService.getMedicineInfo(image);

        return new SuccessResponse<>(result);
    }
}
