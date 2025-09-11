package com.lovediary.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SharedMovieDTO {
    private Long id;
    private String title;
    private String description;
    private String coverUrl;
    private String movieUrl;
    private String fileName;
    private Boolean isPublic;
    private Integer durationMinutes;
    private Integer durationSeconds;
    private Long fileSize;
    private Integer width;
    private Integer height;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 创建者信息
    private Long creatorId;
    private String creatorName;
    private String creatorDisplayName;
    
    // 伴侣信息
    private Long partnerId;
    private String partnerName;
    private String partnerDisplayName;
    
    // 分享链接信息
    private LocalDateTime expiresAt;
}
