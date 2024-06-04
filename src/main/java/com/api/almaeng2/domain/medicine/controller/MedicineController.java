package com.api.almaeng2.domain.medicine.controller;

import com.api.almaeng2.domain.medicine.dto.MedicineListResDto;
import com.api.almaeng2.domain.medicine.dto.PrescriptionListResDto;
import com.api.almaeng2.domain.medicine.service.MedicineService;
import com.api.almaeng2.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/medicine")
public class MedicineController {

    private final MedicineService medicineService;

    @Operation(summary = "처방전 등록", description = "사용자가 받은 처방전을 분석하여 처방전 리스트에 등록합니다.")
    @PostMapping()
    public SuccessResponse<Object> register(@Valid @RequestPart(value = "image")MultipartFile multipartFile, @RequestParam(value = "name") String prescriptionListName) throws IOException {

        String result = medicineService.getPrescriptionInfo(multipartFile, prescriptionListName);

        log.info("result : {}", result);
        return SuccessResponse.ok(result);
    }

    @Operation(summary = "알약 복용 시기 확인", description = "현재 복용하려는 약이 아침, 점심, 저녁 중 알맞는 시간에 복용하는지 확인합니다.")
    @PostMapping("/checkTOD")
    public SuccessResponse getTODInfo(@Valid @RequestPart(value = "image")MultipartFile multipartFile) throws IOException {
        String result = medicineService.getTODInfo(multipartFile);

        return new SuccessResponse(result);
    }

    @Operation(summary = "알약 분석", description = "복용하려는 알약에 대해 분석한 후 상세 정보를 알려줍니다.")
    @PostMapping("/similarity")
    public SuccessResponse<MedicineListResDto> getSimilarityInfo(@Valid @RequestPart(value = "images") List<MultipartFile> multipartFiles) throws IOException{

        MedicineListResDto dto = medicineService.getSimilarityInfo(multipartFiles);

        return new SuccessResponse<>(dto);
    }

    @Operation(summary = "처방전 리스트 조회", description = "사용자가 지금까지 가지고 있는 처방전들을 조회합니다.")
    @GetMapping()
    public SuccessResponse<PrescriptionListResDto> getPrescriptionList(){


        return new SuccessResponse(medicineService.getBoards());
    }

    @Operation(summary = "처방전 약품 상세 조회", description = "사용자가 받은 처방전 중 하나의 특정 약품에 대해 상세하게 조회합니다.")
    @GetMapping("/{prescriptionId}")
    public SuccessResponse<List<MedicineListResDto>> getPill(@PathVariable(value = "prescriptionId", required = true) Long prescriptionId){

        List<MedicineListResDto> pills = medicineService.getPills(prescriptionId);

        return new SuccessResponse(pills);
    }

    @Operation(summary = "처방전 삭제", description = "복용 완료된 처방전에 대하여 삭제합니다.")
    @DeleteMapping("/{prescriptionId}")
    public SuccessResponse<?> deleteList(@PathVariable(value = "prescriptionId", required = true) Long prescriptionId){

        medicineService.deleteBoard(prescriptionId);
        return SuccessResponse.ok("삭제되었습니다.");
    }
}
