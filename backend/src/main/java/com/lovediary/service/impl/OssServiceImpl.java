package com.lovediary.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.lovediary.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class OssServiceImpl implements OssService {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Value("${aliyun.oss.url-prefix}")
    private String urlPrefix;

    @Override
    public String uploadFile(MultipartFile file, String type) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            String fileName = generateFileName(file.getOriginalFilename(), type);
            String objectName = type + "/" + fileName;

            ossClient.putObject(bucketName, objectName, file.getInputStream());

            return urlPrefix + "/" + objectName;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败");
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith(urlPrefix)) {
            return;
        }

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            String objectName = fileUrl.substring(urlPrefix.length() + 1);
            ossClient.deleteObject(bucketName, objectName);
        } catch (Exception e) {
            log.error("文件删除失败", e);
        } finally {
            ossClient.shutdown();
        }
    }

    private String generateFileName(String originalFilename, String type) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "");

        return timestamp + "_" + uuid + extension;
    }
} 