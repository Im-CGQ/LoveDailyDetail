package com.lovediary.service;

import org.springframework.web.multipart.MultipartFile;

public interface OssService {

    String uploadFile(MultipartFile file, String type);

    void deleteFile(String fileUrl);
} 