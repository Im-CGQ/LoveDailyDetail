package com.lovediary.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MovieRoomDetailDTO {
    
    private Long id;
    
    private String roomName;
    
    private String roomCode;
    
    private Double playTime;
    
    private Boolean isPlaying;
    
    private Long lastUpdatedBy;
    
    private LocalDateTime lastUpdatedAt;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // 电影信息
    private MovieDTO movie;
    
    // 创建者信息
    private Long creatorId;
    private String creatorName;
    
    // 成员信息
    private List<MovieRoomMemberDTO> members;
}
