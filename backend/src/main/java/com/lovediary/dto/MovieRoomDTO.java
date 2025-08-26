package com.lovediary.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MovieRoomDTO {
    
    @NotBlank(message = "房间名称不能为空")
    private String roomName;
    
    @NotNull(message = "电影ID不能为空")
    private Long movieId;
}

