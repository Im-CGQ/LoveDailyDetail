package com.lovediary.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class EmailRegisterRequest {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度为2-20个字符")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9_]+$", message = "用户名只能包含中文、字母、数字和下划线")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度为6-20个字符")
    private String password;
    
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
    
    @NotBlank(message = "显示名称不能为空")
    @Size(min = 2, max = 20, message = "显示名称长度为2-20个字符")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9_\\s]+$", message = "显示名称只能包含中文、字母、数字、下划线和空格")
    private String displayName;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "请输入正确的邮箱格式")
    private String email;
    
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "验证码为6位数字")
    private String verificationCode;
}
