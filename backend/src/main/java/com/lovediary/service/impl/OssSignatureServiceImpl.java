package com.lovediary.service.impl;

import com.lovediary.config.OssConfig;
import com.lovediary.service.OssSignatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.codec.binary.Base64;

@Service
@RequiredArgsConstructor
public class OssSignatureServiceImpl implements OssSignatureService {

    private final OssConfig ossConfig;

    @Override
    public Map<String, String> getUploadSignature(String fileName, String fileType) {
        try {
            // 生成唯一的文件名
            String fileExtension = getFileExtension(fileName);
            String uniqueFileName = generateUniqueFileName(fileType, fileExtension);
            
            // 设置过期时间（1小时后过期）
            Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
            
            // 构建策略
            String policy = buildPolicy(uniqueFileName, expiration);
            
            // 生成签名
            String signature = generateSignature(policy);
            
            // 构建返回结果
            Map<String, String> result = new HashMap<>();
            result.put("accessid", ossConfig.getAccessKeyId());
            result.put("policy", policy);
            result.put("signature", signature);
            result.put("dir", getFileDir(fileType));
            result.put("host", ossConfig.getUrlPrefix());
            result.put("expire", String.valueOf(expiration.getTime() / 1000));
            result.put("fileName", uniqueFileName);
            result.put("urlPrefix", ossConfig.getUrlPrefix());
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("生成上传签名失败: " + e.getMessage());
        }
    }
    
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex);
        }
        return "";
    }
    
    private String generateUniqueFileName(String fileType, String extension) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return fileType + "/" + timestamp + "_" + uuid + extension;
    }
    
    private String getFileDir(String fileType) {
        switch (fileType.toLowerCase()) {
            case "image":
                return "image/";
            case "video":
                return "video/";
            case "music":
                return "music/";
            case "avatar":
                return "avatar/";
            default:
                return "others/";
        }
    }
    
    private String buildPolicy(String fileName, Date expiration) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        String expirationStr = sdf.format(expiration);
        String fileType = fileName.substring(0, fileName.indexOf("/"));
        String dir = getFileDir(fileType);
        
        String policy = "{\"expiration\":\"" + expirationStr + "\"," +
                "\"conditions\":[" +
                "{\"bucket\":\"" + ossConfig.getBucketName() + "\"}," +
                "[\"starts-with\",\"$key\",\"" + dir + "\"]" +
                "]}";
        
        return Base64.encodeBase64String(policy.getBytes(StandardCharsets.UTF_8));
    }
    
    private String generateSignature(String policy) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA1");
            byte[] keyBytes = ossConfig.getAccessKeySecret().getBytes(StandardCharsets.UTF_8);
            hmac.init(new SecretKeySpec(keyBytes, "HmacSHA1"));
            byte[] signatureBytes = hmac.doFinal(policy.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(signatureBytes);
        } catch (Exception e) {
            throw new RuntimeException("生成签名失败", e);
        }
    }
} 