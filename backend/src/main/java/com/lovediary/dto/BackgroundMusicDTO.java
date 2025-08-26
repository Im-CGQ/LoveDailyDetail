package com.lovediary.dto;

import lombok.Data;

@Data
public class BackgroundMusicDTO {
    private Long id;
    private String musicUrl;
    private String fileName;
    private String title;
    private String artist;
    private Integer duration;
}
