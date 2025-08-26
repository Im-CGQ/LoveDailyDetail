package com.lovediary.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SharedDiaryDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate date;
    private List<ImageInfoDTO> images;
    private List<VideoInfoDTO> videos;
    private List<DiaryBackgroundMusicDTO> backgroundMusic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 用户信息
    private Long userId;
    private String userName;
    private String userDisplayName;
    
    // 分享链接信息
    private LocalDateTime expiresAt;
}
