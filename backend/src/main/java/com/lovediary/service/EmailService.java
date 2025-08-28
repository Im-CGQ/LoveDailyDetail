package com.lovediary.service;

import com.lovediary.dto.SmsSendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
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
    private boolean checkSendFrequency(String email) {
        LocalDateTime lastSendTime = sendRecords.get(email);
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
    private void updateSendRecord(String email) {
        sendRecords.put(email, LocalDateTime.now());
    }
    
    /**
     * 发送验证码
     */
    public SmsSendResult sendVerificationCode(String email) {
        try {
            // 检查发送频率限制
            if (!checkSendFrequency(email)) {
                System.err.println("发送频率过高，邮箱: " + email + ", 请1分钟后再试");
                return SmsSendResult.frequencyLimit();
            }
            
            String code = generateVerificationCode();
            
            // 创建邮件消息
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("美好回忆 - 邮箱验证码");
            message.setText("您的验证码是：" + code + "，有效期5分钟。请勿泄露给他人。");
            
            // 发送邮件
            mailSender.send(message);
            
            // 发送成功，存储验证码，有效期5分钟
            verificationCodes.put(email, new VerificationCode(code, LocalDateTime.now().plusMinutes(5)));
            // 更新发送记录
            updateSendRecord(email);
            
            System.out.println("邮件发送成功，邮箱: " + email + ", 验证码: " + code);
            return SmsSendResult.success("EMAIL_" + System.currentTimeMillis());
            
        } catch (Exception e) {
            System.err.println("发送验证码异常，邮箱: " + email + ", 错误信息: " + e.getMessage());
            return SmsSendResult.failure(e.getMessage(), "EMAIL_SEND_ERROR", null);
        }
    }
    
    /**
     * 验证验证码
     */
    public boolean verifyCode(String email, String code) {
        VerificationCode storedCode = verificationCodes.get(email);
        if (storedCode == null) {
            return false;
        }
        
        // 检查验证码是否过期
        if (LocalDateTime.now().isAfter(storedCode.getExpireTime())) {
            verificationCodes.remove(email);
            return false;
        }
        
        // 检查验证码是否匹配
        if (storedCode.getCode().equals(code)) {
            // 验证成功后删除验证码
            verificationCodes.remove(email);
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
