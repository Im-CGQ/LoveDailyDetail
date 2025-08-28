package com.lovediary.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UpdateUserRequest {
    
    @NotBlank(message = "显示名称不能为空")
    @Size(min = 1, max = 50, message = "显示名称长度必须在1-50个字符之间")
    private String displayName;
    
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;
    
    @Size(min = 6, max = 20, message = "确认密码长度必须在6-20个字符之间")
    private String confirmPassword;
}
