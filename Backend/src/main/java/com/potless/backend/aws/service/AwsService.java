package com.potless.backend.aws.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsService {

    private final AmazonS3 s3Client;

    @Value("${aws.s3-bucket-name}")
    private String bucketName;

    public void downloadFile(String keyName) throws IOException {
        S3Object s3object = s3Client.getObject(bucketName, keyName);
        S3ObjectInputStream inputStream = s3object.getObjectContent();
        FileOutputStream outputStream = new FileOutputStream(new File(keyName));
        byte[] read_buf = new byte[1024];
        int read_len = 0;
        while ((read_len = inputStream.read(read_buf)) > 0) {
            outputStream.write(read_buf, 0, read_len);
        }
        outputStream.close();
        inputStream.close();
    }

    public Map<String, String> uploadFileToS3(MultipartFile file) throws IOException {
        String fileName = "검증전/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File localFile = convertMultiPartToFile(file);
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, localFile));
        if (!localFile.delete()) {
            log.error("Failed to delete temporary file: {}", localFile.getPath());
        } else {
            log.info("Temporary file deleted successfully: {}", localFile.getPath());
        }
        String fileUrl = s3Client.getUrl(bucketName, fileName).toString();
        Map<String, String> fileData = new HashMap<>();
        fileData.put(fileName, fileUrl);
        return fileData;
    }


    public String moveFileToVerified(String sourceKey, String destinationKey) {
        // 파일을 새 위치로 복사
        CopyObjectRequest copyObjRequest = new CopyObjectRequest(bucketName, sourceKey, bucketName, destinationKey);
        s3Client.copyObject(copyObjRequest);

        // 원본 파일 삭제
        DeleteObjectRequest deleteObjRequest = new DeleteObjectRequest(bucketName, sourceKey);
        s3Client.deleteObject(deleteObjRequest);

        // 새 위치의 파일 URL 반환
        return s3Client.getUrl(bucketName, destinationKey).toString();
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}