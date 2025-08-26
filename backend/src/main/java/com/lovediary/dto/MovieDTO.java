package com.lovediary.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MovieDTO {
    
    private Long id;
    
    @NotBlank(message = "电影标题不能为空")
    private String title;
    
    private String description;
    
    private String coverUrl;
    
    private String movieUrl;
    
    private String fileName;
    
    @NotNull(message = "是否公开不能为空")
    private Boolean isPublic;
    
    private Integer durationMinutes;
    
    private Long fileSize;
    
    private Integer width;
    
    private Integer height;
    
    // 创建者信息
    private Long creatorId;
    private String creatorName;
    
    // 伴侣信息
    private Long partnerId;
    private String partnerName;
    
    // 时间信息
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
}
