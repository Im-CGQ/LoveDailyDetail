package com.lovediary.dto;

import lombok.Data;

@Data
public class LoginResponse {
    
    private boolean success;
    private String message;
    private String token;
    private UserInfo user;
    
    @Data
    public static class UserInfo {
        private String username;
        private String role;
        private String displayName;
    }
} 