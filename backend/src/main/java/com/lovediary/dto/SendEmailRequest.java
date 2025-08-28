package com.lovediary.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class SendEmailRequest {
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "请输入正确的邮箱格式")
    private String email;
}
