package com.contentmanager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.bucketName}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(String idCapture, String base64Content, String fileName) {
        String idFileFolder = UUID.randomUUID().toString();
        String key = idCapture + "/" + idFileFolder + "/" + fileName;
        
        byte[] fileContent = Base64.getDecoder().decode(base64Content);
        
        s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build(),
                RequestBody.fromBytes(fileContent));
        
        return idFileFolder;
    }

    public List<String> listFiles(String idCapture) {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(idCapture + "/")
                .build();

        return s3Client.listObjectsV2(request).contents().stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
    }

    public void deleteFile(String idCapture, String idFile) {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(idCapture + "/" + idFile + "/")
                .build();

        List<S3Object> objects = s3Client.listObjectsV2(request).contents();
        
        for (S3Object object : objects) {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(object.key())
                    .build());
        }
    }

    public String downloadFile(String idCapture, String idFile) {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(idCapture + "/" + idFile + "/")
                .build();

        List<S3Object> objects = s3Client.listObjectsV2(request).contents();
        
        if (objects.isEmpty()) {
            throw new RuntimeException("File not found");
        }

        ResponseInputStream<GetObjectResponse> s3Object  = s3Client.getObject(GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objects.get(0).key())
                .build());

        Long contentLength = s3Object.response().contentLength();


        GetObjectRequest requestFile = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objects.get(0).key())
                .build();

        try (ResponseInputStream<GetObjectResponse> inputStream = s3Client.getObject(requestFile);

             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

                byte[] data = new byte[Math.toIntExact(contentLength)];
                int bytesRead;
                while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, bytesRead);
                }

             return Base64.getEncoder().encodeToString(buffer.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
} 