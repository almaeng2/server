package com.api.almaeng2.global.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.api.almaeng2.global.exception.ApiException;
import com.api.almaeng2.global.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import static com.api.almaeng2.global.exception.ErrorType.*;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3UploadService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public List<String> uploadFiles(List<MultipartFile> files, String folderName) throws IOException{
        List<String> result = new ArrayList<>();
        if(files.isEmpty())
            throw new ApiException(NO_FILE_CONTAINED);

        for(MultipartFile file : files){
            result.add(upload(file, folderName));
        }
        return result;
    }

    public String upload(MultipartFile file, String folderName) throws IOException{
        File uploadFile = convert(file)
                .orElseThrow(() -> new ApiException(S3_CONVERT));

        return upload(uploadFile, folderName);
    }

    private Optional<File> convert(MultipartFile file) throws IOException {

        String fileName = validFileName(file.getOriginalFilename());
        File convertFile = new File(fileName);
        if(convertFile.createNewFile()){
            log.info("create");
            try(FileOutputStream fos = new FileOutputStream(convertFile)){
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    private String validFileName(final String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            return UUID.randomUUID() + originalFilename;
        }
        return originalFilename;
    }

    private String upload(File uploadFile, String folderName){
        String fileName = folderName + "/" + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);

        removeNewFile(uploadFile);

        return uploadImageUrl;
    }

    private String putS3(File uploadFile, String fileName){
        amazonS3.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );
        return amazonS3.getUrl(bucket, fileName).toString();
    }


    // -------------------- delete -----------------------------

    private void removeNewFile(File targetFile){
        if(targetFile.delete())
            log.info("파일이 삭제되었습니다.");
        else{
            log.info("파일이 삭제되지 못했습니다.");
        }
    }
    public void deleteFilesIntDirectory(String directoryPath){
        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request().withBucketName(bucket).withPrefix(directoryPath+"/");
        ListObjectsV2Result listObjectsV2Result = amazonS3.listObjectsV2(listObjectsV2Request);
        ListIterator<S3ObjectSummary> listIterator = listObjectsV2Result.getObjectSummaries().listIterator();

        while (listIterator.hasNext()){
            S3ObjectSummary objectSummary = listIterator.next();
            DeleteObjectRequest request = new DeleteObjectRequest(bucket,objectSummary.getKey());
            amazonS3.deleteObject(request);
            System.out.println("Deleted " + objectSummary.getKey());
        }
    }

}
