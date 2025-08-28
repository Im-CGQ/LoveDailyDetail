package com.lovediary.service;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovediary.dto.SmsSendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class SmsService {
    
    @Autowired
    private Client smsClient;
    
    @Value("${aliyun.sms.template-code}")
    private String templateCode;
    
    @Value("${aliyun.sms.sign-name}")
    private String signName;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 存储验证码的Map，实际项目中应该使用Redis
    private final Map<String, VerificationCode> verificationCodes = new ConcurrentHashMap<>();
    
    // 存储发送记录的Map，用于频率限制
    private final Map<String, LocalDateTime> sendRecords = new ConcurrentHashMap<>();
    
    /**
     * 生成验证码
     */
    public String generateVerificationCode() {
        // 生成6位数字验证码
        return String.format("%06d", (int)(Math.random() * 1000000));
    }
    
    /**
     * 检查发送频率限制
     */
    private boolean checkSendFrequency(String phone) {
        LocalDateTime lastSendTime = sendRecords.get(phone);
        if (lastSendTime != null) {
            // 检查是否在1分钟内重复发送
            if (LocalDateTime.now().minusMinutes(1).isBefore(lastSendTime)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 更新发送记录
     */
    private void updateSendRecord(String phone) {
        sendRecords.put(phone, LocalDateTime.now());
    }
    
    /**
     * 发送验证码
     */
    public SmsSendResult sendVerificationCode(String phone) {
        try {
            // 检查发送频率限制
            if (!checkSendFrequency(phone)) {
                System.err.println("发送频率过高，手机号: " + phone + ", 请1分钟后再试");
                return SmsSendResult.frequencyLimit();
            }
            
            String code = generateVerificationCode();
            
            // 创建短信请求
            SendSmsRequest request = new SendSmsRequest()
                    .setPhoneNumbers(phone)
                    .setSignName(signName)
                    .setTemplateCode(templateCode)
                    .setTemplateParam(objectMapper.writeValueAsString(new HashMap<String, String>() {{
                        put("code", code);
                    }}));
            
            // 发送短信
            SendSmsResponse response = smsClient.sendSms(request);
            
            // 检查发送结果
            if ("OK".equals(response.getBody().getCode())) {
                // 发送成功，存储验证码，有效期5分钟
                verificationCodes.put(phone, new VerificationCode(code, LocalDateTime.now().plusMinutes(5)));
                // 更新发送记录
                updateSendRecord(phone);
                System.out.println("短信发送成功，手机号: " + phone + ", 验证码: " + code);
                return SmsSendResult.success(response.getBody().getRequestId());
            } else {
                String errorMessage = response.getBody().getMessage();
                String errorCode = response.getBody().getCode();
                
                // 处理特定的错误码
                if ("isv.SMS_SIGNATURE_ILLEGAL".equals(errorCode)) {
                    errorMessage = "短信签名不存在或未审核通过，请联系管理员";
                } else if ("isv.SMS_TEMPLATE_ILLEGAL".equals(errorCode)) {
                    errorMessage = "短信模板不存在或未审核通过，请联系管理员";
                } else if ("isv.INVALID_PARAMETERS".equals(errorCode)) {
                    errorMessage = "参数错误，请检查配置";
                } else if ("isv.OUT_OF_SERVICE".equals(errorCode)) {
                    errorMessage = "业务停机，请联系客服";
                } else if ("isv.INVALID_JSON_PARAM".equals(errorCode)) {
                    errorMessage = "JSON参数不合法";
                } else if ("isv.BLACK_KEY_CONTROL_LIMIT".equals(errorCode)) {
                    errorMessage = "黑名单管控";
                } else if ("isv.PARAM_LENGTH_LIMIT".equals(errorCode)) {
                    errorMessage = "参数超出长度限制";
                } else if ("isv.PARAM_NOT_SUPPORT_URL".equals(errorCode)) {
                    errorMessage = "不支持URL";
                } else if ("isv.AMOUNT_NOT_ENOUGH".equals(errorCode)) {
                    errorMessage = "账户余额不足";
                }
                
                System.err.println("短信发送失败，手机号: " + phone + ", 错误码: " + errorCode + ", 错误信息: " + errorMessage);
                return SmsSendResult.failure(errorMessage, errorCode, response.getBody().getRequestId());
            }
        } catch (Exception e) {
            System.err.println("发送验证码异常，手机号: " + phone + ", 错误信息: " + e.getMessage());
            return SmsSendResult.failure(e.getMessage(), "UNKNOWN_ERROR", null);
        }
    }
    
    /**
     * 验证验证码
     */
    public boolean verifyCode(String phone, String code) {
        VerificationCode storedCode = verificationCodes.get(phone);
        if (storedCode == null) {
            return false;
        }
        
        // 检查验证码是否过期
        if (LocalDateTime.now().isAfter(storedCode.getExpireTime())) {
            verificationCodes.remove(phone);
            return false;
        }
        
        // 检查验证码是否匹配
        if (storedCode.getCode().equals(code)) {
            // 验证成功后删除验证码
            verificationCodes.remove(phone);
            return true;
        }
        
        return false;
    }
    
    /**
     * 验证码内部类
     */
    private static class VerificationCode {
        private final String code;
        private final LocalDateTime expireTime;
        
        public VerificationCode(String code, LocalDateTime expireTime) {
            this.code = code;
            this.expireTime = expireTime;
        }
        
        public String getCode() {
            return code;
        }
        
        public LocalDateTime getExpireTime() {
            return expireTime;
        }
    }
}
