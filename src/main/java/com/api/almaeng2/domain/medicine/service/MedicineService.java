package com.api.almaeng2.domain.medicine.service;

import com.api.almaeng2.domain.household.dto.LambdaResDto;
import com.api.almaeng2.domain.medicine.dto.MedicineListResDto;
import com.api.almaeng2.domain.medicine.dto.PrescriptionListResDto;
import com.api.almaeng2.domain.medicine.dto.PrescriptionResDto;
import com.api.almaeng2.domain.medicine.entity.Classification;
import com.api.almaeng2.domain.medicine.entity.Prescription;
import com.api.almaeng2.domain.medicine.entity.PrescriptionList;
import com.api.almaeng2.domain.medicine.entity.PrescriptionsList;
import com.api.almaeng2.domain.medicine.repository.*;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class MedicineService {

    private final PillRepository pillRepository;
    private final PrescriptionListRepository prescriptionListRepository;
    private final PrescriptionsListRepository prescriptionsListRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final ClassificationRepository classificationRepository;
    private final S3UploadService s3UploadService;

    @Value("${cloud.aws.lambda.prescriptionOCR}")
    private String lambda2;

    @Value("${cloud.aws.lambda.todOCR}")
    private String lambda3;

    @Value("${cloud.aws.lambda.similarity}")
    private String lambda4;

    public Member getCurrentMember(){
        return SecurityUtil.currentMember();
    }

    // ------------------------------------------ 딥러닝 모델을 통해 처방전 만들기 -----------------------------------

    //과정 1. 멤버 찾고 lambda에서 모델 호출하여 나온 결과들 String[]으로 담기
    //2. 없는 약들은 건너뛰고 있는 약들을 저장 및 classification 분류하여 하나의 처방전에 집어넣기
    public String getPrescriptionInfo(MultipartFile image, String listName) throws IOException {
        Member member = getCurrentMember();
        String folder = "prescription/test/";

        s3UploadService.deleteFilesIntDirectory(folder);
        String result = "none";
        if(image != null && !image.isEmpty()){
            String fileUrl = s3UploadService.upload(image, folder);
            WebClient webClient = WebClient.builder()
                    .baseUrl(lambda2)
                    .build();

            LambdaResDto dto = webClient.post()
                    .retrieve()
                    .bodyToMono(LambdaResDto.class)
                    .block();
            result = dto.getBody();
        }

        getRightName(result);
        String[] list = getName(result);

        PrescriptionList prescriptionList = PrescriptionList.builder()
                .name(listName)
                .member(member)
                .build();
        prescriptionListRepository.save(prescriptionList);

        for(String name : list){
            Prescription prescription = prescriptionRepository.findByName(name).orElseThrow(() -> new ApiException(ErrorType._NOT_FOUND_PILL));
            PrescriptionsList prescriptionsList = PrescriptionsList.builder()
                    .prescriptionList(prescriptionList)
                    .prescription(prescription)
                    .build();
            prescriptionsListRepository.save(prescriptionsList);
        }


        return "처방전 등록이 완료되었습니다!";
    }

    private String[] getName(String st) {
        List<String> list = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < st.length(); i++) {
            if (st.charAt(i) == '"')
                count += 1;
            if (count > 2 && st.charAt(i) == '"') {
                i += 1;
                String find = "";
                while (true) {
                    if (st.charAt(i) == '"') {
                        count += 1;
                        break;
                    }
                    find = find + st.charAt(i);
                    i += 1;
                }
                list.add(find);
            }
        }

        String[] result = new String[list.size()];
        for(int i=0;i<list.size();i++){
            result[i] = list.get(i);
        }
        return result;
    }

    private Boolean getRightName(String name){
        String regex = "\"result\":\\s*\"([^\"]+)\"";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);


        if(matcher.find()){
            if(matcher.group(1).equals("None"))
                throw new ApiException(ErrorType._NOT_CORRECT_DIRECTION);
            else return true;
        }

        return false;
    }

    // -------------------------------------- 복용 시기 확인하기 -----------------------------------------

    public String getTODInfo(MultipartFile multipartFile) throws IOException {
        Member member = getCurrentMember();
        String folder = "todocr/test";

        s3UploadService.deleteFilesIntDirectory(folder);
        String result = "none";
        if(multipartFile != null && !multipartFile.isEmpty()){
            String fileUrl = s3UploadService.upload(multipartFile, folder);
            WebClient webClient = WebClient.builder()
                    .baseUrl(lambda3)
                    .build();

            LambdaResDto dto = webClient.post()
                    .retrieve()
                    .bodyToMono(LambdaResDto.class)
                    .block();
            result = dto.getBody();
        }
        result = getTimeInfo(result);

        return result;
    }

    private String getTimeInfo(String name){
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

    // -------------------------------------- 알약 유사도 분석하기 -----------------------------------------

    public MedicineListResDto getSimilarityInfo(List<MultipartFile> multipartFiles) throws IOException {
        Member member = getCurrentMember();
        String folder = "similarity/test";

        s3UploadService.deleteFilesIntDirectory(folder);
        String result = "none";
        if(multipartFiles != null && !multipartFiles.isEmpty()){
            List<String> list = s3UploadService.uploadFiles(multipartFiles, folder);
            WebClient webClient = WebClient.builder()
                    .baseUrl(lambda4)
                    .build();

            LambdaResDto dto = webClient.post()
                    .retrieve()
                    .bodyToMono(LambdaResDto.class)
                    .block();
            result = dto.getBody();
        }

        result = getSimilarity(result);
        log.info("result: {}", result);
        Prescription prescription = prescriptionRepository.findByName(result).orElseThrow(() -> new ApiException(ErrorType._NOT_FOUND_PILL));

        MedicineListResDto dto = MedicineListResDto.builder()
                .name(prescription.getName())
                .shape(prescription.getShape())
                .effect(prescription.getEffect())
                .caution(prescription.getCaution())
                .imageUrl(prescription.getImage().getImageUrl())
                .classification(findClasses(prescription))
                .build();

        return dto;
    }

    private String getSimilarity(String name){
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

  // --------------------------------------------사용자 처방전 리스트 불러오기---------------------------------------

    @Transactional(readOnly = true)
    public PrescriptionListResDto getBoards(){
        Member member = getCurrentMember();

        List<PrescriptionList> list = prescriptionListRepository.findAllByMemberId(member.getUserId());
        List<PrescriptionResDto> result = new ArrayList<>();

        for(PrescriptionList prescriptionList : list){
            result.add(PrescriptionResDto.builder()
                    .listId(prescriptionList.getId())
                    .name(prescriptionList.getName())
                    .prescribedTime(convertDate(prescriptionList.getCreatedDate()))
                    .build());
        }

        PrescriptionListResDto dto = PrescriptionListResDto.builder()
                .lists(result)
                .build();
        return dto;
    }

    private String convertDate(LocalDateTime createdTime){
        String time = String.valueOf(createdTime);
        time = time.substring(0,10);
        return time;
    }

    // ----------------------------------사용자 처방전별 약 리스트 불러오기-------------------------------------------


    @Transactional(readOnly = true)
    public List<MedicineListResDto> getPills(Long prescriptionListId){
        Member member = getCurrentMember();


        PrescriptionList list = prescriptionListRepository.findById(prescriptionListId).orElseThrow(() -> new ApiException(ErrorType._NOT_FOUND_PRESCRIPTION));
        if(isMine(member.getUserId(), list)==Boolean.FALSE){
            throw new ApiException(ErrorType._NOT_MINE);
        }
        List<Long> prescriptions = prescriptionsListRepository.findAllByPrescriptionListId(prescriptionListId)
                .orElseThrow(() -> new ApiException(ErrorType._NOT_FOUND_PILL));

        List<MedicineListResDto> result = new ArrayList<>();
        for(Long prescriptionId : prescriptions){
            Prescription source = prescriptionRepository.findById(prescriptionId).orElseThrow(() -> new ApiException(ErrorType._NOT_FOUND_PILL));
            result.add(MedicineListResDto.builder()
                    .name(source.getName())
                    .shape(source.getShape())
                    .effect(source.getEffect())
                    .caution(source.getCaution())
                    .imageUrl(source.getImage().getImageUrl())
                    .classification(findClasses(source))
                    .build());
        }

        return result;
    }

    private List<String> findClasses(Prescription p){
        List<Long> classes = pillRepository.findAllByPrescriptionId(p.getId()).orElseThrow(() -> new ApiException(ErrorType._NOT_FOUND_CLASS));
        List<String> result = new ArrayList<>();

        log.info("분류 찾는중");
        for(Long classId : classes){
            Classification classification = classificationRepository.findByClassId(classId).orElseThrow(() -> new ApiException(ErrorType._NOT_FOUND_CLASS));
            result.add(classification.getClassName());
        }

        return result;
    }

    private static Boolean isMine(String userId, PrescriptionList prescriptionList){
        if(prescriptionList.getMember().getUserId().equals(userId))
            return Boolean.TRUE;
        return Boolean.FALSE;
    }

    // ---------------------------------------------- remove ---------------------------------------------------------------

    public void deleteBoard(Long prescriptionListId){
        Member member = getCurrentMember();

        PrescriptionList list = prescriptionListRepository.findById(prescriptionListId).orElseThrow(() -> new ApiException(ErrorType._NOT_FOUND_PRESCRIPTION));
        if(isMine(member.getUserId(), list)==Boolean.FALSE){
            throw new ApiException(ErrorType._NOT_MINE);
        }

        prescriptionsListRepository.deleteAllByPrescriptionListId(prescriptionListId);
        prescriptionListRepository.delete(list);
    }
}
