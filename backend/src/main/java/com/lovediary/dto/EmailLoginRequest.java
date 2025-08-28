package com.lovediary.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class EmailLoginRequest {
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "请输入正确的邮箱格式")
    private String email;
    
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "验证码为6位数字")
    private String verificationCode;
    
    private boolean rememberMe = false;
}
