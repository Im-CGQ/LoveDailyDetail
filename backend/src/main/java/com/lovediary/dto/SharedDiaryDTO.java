package com.lovediary.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class SharedDiaryDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate date;
    private List<String> images;
    private List<String> videos;
    private String backgroundMusic;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    
    // 用户信息
    private Long userId;
    private String userName;
    private String userDisplayName;
}
