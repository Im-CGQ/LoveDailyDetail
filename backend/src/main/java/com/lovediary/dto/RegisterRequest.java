package com.lovediary.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RegisterRequest {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;
    
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
    
    @NotBlank(message = "显示名称不能为空")
    @Size(max = 50, message = "显示名称不能超过50个字符")
    private String displayName;
} 