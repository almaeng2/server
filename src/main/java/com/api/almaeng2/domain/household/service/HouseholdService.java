package com.api.almaeng2.domain.household.service;

import com.api.almaeng2.domain.base.image.service.ImageService;
import com.api.almaeng2.domain.household.dto.HouseholdResDto;
import com.api.almaeng2.domain.household.dto.LambdaResDto;
import com.api.almaeng2.domain.household.entity.HouseholdMedicine;
import com.api.almaeng2.domain.household.repository.HouseholdMedicineRepository;
import com.api.almaeng2.domain.household.repository.HouseholdPillRepository;
import com.api.almaeng2.domain.medicine.entity.Classification;
import com.api.almaeng2.domain.medicine.repository.ClassificationRepository;
import com.api.almaeng2.domain.member.entity.Member;
import com.api.almaeng2.global.config.S3UploadService;
import com.api.almaeng2.global.exception.ApiException;
import com.api.almaeng2.global.exception.ErrorType;
import com.api.almaeng2.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class HouseholdService {

    private final S3UploadService s3UploadService;
    private final ImageService imageService;
    private final HouseholdMedicineRepository householdMedicineRepository;
    private final HouseholdPillRepository householdPillRepository;
    private final ClassificationRepository classificationRepository;

    @Value("${cloud.aws.lambda.housepill}")
    private String lambda1;

    public Member getCurrentMember(){
        return SecurityUtil.currentMember();
    }

    // 상비약을 촬영하여 나온 텍스트값 약품 정보 반환
    public HouseholdResDto getMedicineInfo(MultipartFile image) throws IOException {
        Member member = getCurrentMember();
        String folder = "housepill/test/for test/";

        s3UploadService.deleteFilesIntDirectory(folder);

        String result = "none";
        if(image != null && !image.isEmpty()) {
            String fileUrl = s3UploadService.upload(image, folder);
            imageService.register(fileUrl, "안전상비약");
            WebClient webClient = WebClient.builder()
                    .baseUrl(lambda1)
                    .build();

            LambdaResDto dto = webClient.post()
                    .retrieve()
                    .bodyToMono(LambdaResDto.class)
                    .block();
            result = dto.getBody();
        }
        log.info("hi {}", result);
        result = getRightName(result);

        HouseholdMedicine medicine = householdMedicineRepository.findByName(result).orElseThrow(() -> new ApiException(ErrorType._NOT_FOUND_PILL));


        HouseholdResDto dto = HouseholdResDto.builder()
                .name(medicine.getName())
                .shape(medicine.getShape())
                .effect(medicine.getEffect())
                .caution(medicine.getCaution())
                .imageUrl(medicine.getImage().getImageUrl())
                .classification(findClasses(medicine))
                .build();

        return dto;
    }

    private String getRightName(String name){
        String regex = "\"result\":\\s*\"([^\"]+)\"";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);


        if(matcher.find()){
            if(matcher.group(1).equals("None"))
                throw new ApiException(ErrorType._NOT_CORRECT_DIRECTION);
            else return matcher.group(1);
        }
        else{
            throw new ApiException(ErrorType._NOT_FOUND_PILL);
        }
    }

    private List<String> findClasses(HouseholdMedicine h){
        List<Long> classes = householdPillRepository.findAllByPrescriptionId(h.getId()).orElseThrow(() -> new ApiException(ErrorType._NOT_FOUND_CLASS));
        List<String> result = new ArrayList<>();

        log.info("분류 찾는중");
        for(Long classId : classes){
            Classification classification = classificationRepository.findByClassId(classId).orElseThrow(() -> new ApiException(ErrorType._NOT_FOUND_CLASS));
            result.add(classification.getClassName());
        }
        log.info("찾았습니다!");

        return result;
    }
}
