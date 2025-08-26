package com.lovediary.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MoviePlaybackDTO {
    
    @NotNull(message = "播放时间不能为空")
    private Double currentTime;
    
    @NotNull(message = "播放状态不能为空")
    private Boolean isPlaying;
}

