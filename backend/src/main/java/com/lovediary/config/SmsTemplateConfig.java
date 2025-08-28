package com.lovediary.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aliyun.sms")
public class SmsTemplateConfig {
    
    private String accessKeyId;
    private String accessKeySecret;
    private String templateCode;
    private String signName;
    private String regionId;
    
    // 验证码模板参数
    private String codeParamName = "code";
    
    // 短信模板类型
    public static final String TEMPLATE_TYPE_VERIFICATION = "verification";
    public static final String TEMPLATE_TYPE_NOTIFICATION = "notification";
    
    // Getters and Setters
    public String getAccessKeyId() {
        return accessKeyId;
    }
    
    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }
    
    public String getAccessKeySecret() {
        return accessKeySecret;
    }
    
    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }
    
    public String getTemplateCode() {
        return templateCode;
    }
    
    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }
    
    public String getSignName() {
        return signName;
    }
    
    public void setSignName(String signName) {
        this.signName = signName;
    }
    
    public String getRegionId() {
        return regionId;
    }
    
    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }
    
    public String getCodeParamName() {
        return codeParamName;
    }
    
    public void setCodeParamName(String codeParamName) {
        this.codeParamName = codeParamName;
    }
}
