package com.lovediary.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LetterDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime unlockTime;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String receiverName;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isUnlocked; // 是否已解锁
    private Long remainingSeconds; // 剩余解锁时间（秒）
}
