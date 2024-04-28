package com.api.almaeng2.global.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
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
import java.util.List;
import java.util.UUID;

import static com.api.almaeng2.global.exception.ErrorType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3UploadService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${bucket.dirname}")
    private String dirName;

    public List<String> uploadMultipartFiles(final List<MultipartFile> multipartFiles){
        validFileNumber(multipartFiles.size());
        return multipartFiles.stream()
                .map(this::uploadSingleFile)
                .toList();
    }

    private void validFileNumber(final int size){
        if(size > 10){
            throw new ApiException(EXCEEDING_FILE_COUNT);
        }
    }

    private String uploadSingleFile(final MultipartFile multipartFile){
        try{
            File uploadFile = convert(multipartFile);
            return upload(uploadFile, dirName);
        } catch (IOException e){
            throw new ApiException(S3_CONNECT);
        }
    }

    private File convert(final MultipartFile file) throws IOException{
        String originalFilename = file.getOriginalFilename();
        originalFilename = validFileName(originalFilename);

        File convertFile = new File(originalFilename);
        validGenerateLocalFile(convertFile);

        try(FileOutputStream fos = new FileOutputStream(convertFile)){
            fos.write(file.getBytes());
        }
        return convertFile;
    }

    private String validFileName(final String originalFilename){
        if(!StringUtils.hasText(originalFilename)){
            return UUID.randomUUID() + getFileExtension(originalFilename);
        }
        return originalFilename;
    }

    private String getFileExtension(String filename){
        int dotIndex = filename.lastIndexOf('.');
        if(dotIndex > 0 && dotIndex < filename.length() - 1){
            return filename.substring(dotIndex);
        }
        return "";
    }

    private void validGenerateLocalFile(final File convertFile) throws IOException{
        if(!convertFile.createNewFile()){
            throw new ApiException(S3_CONVERT);
        }
    }

    private String upload(final File uploadFile, final String dirName){
        String fileName = dirName + "/" + uploadFile.getName();
        String uploadIamgeUrl = putS3(uploadFile, fileName);

        removeNewFile(uploadFile);
        return uploadIamgeUrl;
    }

    private String putS3(final File uploadFile, final String fileName){
        amazonS3.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(final File targetFile){
        if(targetFile.delete()){
            log.info("파일이 삭제되었습니다.");
        }
        else{
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

}
