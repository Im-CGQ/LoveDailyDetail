package com.lovediary.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MovieRoomMemberDTO {
    
    private Long id;
    
    private Long userId;
    
    private String username;
    
    private String displayName;
    
    private LocalDateTime joinedAt;
    
    private Boolean isOnline;
    
    // 用户对象，用于前端显示
    private UserInfo user;
    
    @Data
    public static class UserInfo {
        private Long id;
        private String username;
        private String displayName;
    }
}
