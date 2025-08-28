package com.lovediary.dto;

import lombok.Data;

@Data
public class SmsSendResult {
    private boolean success;
    private String message;
    private String code;
    private String requestId;
    
    public static SmsSendResult success(String requestId) {
        SmsSendResult result = new SmsSendResult();
        result.setSuccess(true);
        result.setMessage("短信发送成功");
        result.setRequestId(requestId);
        return result;
    }
    
    public static SmsSendResult failure(String message, String code, String requestId) {
        SmsSendResult result = new SmsSendResult();
        result.setSuccess(false);
        result.setMessage(message);
        result.setCode(code);
        result.setRequestId(requestId);
        return result;
    }
    
    public static SmsSendResult frequencyLimit() {
        SmsSendResult result = new SmsSendResult();
        result.setSuccess(false);
        result.setMessage("发送频率过高，请1分钟后再试");
        result.setCode("FREQUENCY_LIMIT");
        return result;
    }
}
