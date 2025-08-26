package com.lovediary.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SharedLetterDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime unlockTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isRead;
    
    // 发送者信息
    private Long senderId;
    private String senderName;
    private String senderDisplayName;
    
    // 接收者信息
    private Long receiverId;
    private String receiverName;
    private String receiverDisplayName;
    
    // 分享链接信息
    private LocalDateTime expiresAt;
}
