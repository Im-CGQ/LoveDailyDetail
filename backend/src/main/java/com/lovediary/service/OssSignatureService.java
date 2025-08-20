package com.lovediary.service;

import java.util.Map;

public interface OssSignatureService {
    /**
     * 获取OSS上传签名
     * @param fileName 文件名
     * @param fileType 文件类型 (image, video, music)
     * @return 包含签名信息的Map
     */
    Map<String, String> getUploadSignature(String fileName, String fileType);
} 