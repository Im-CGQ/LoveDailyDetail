package com.lovediary.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public class DiaryDTO {

    private Long id;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotNull(message = "日期不能为空")
    private LocalDate date;

    @NotBlank(message = "描述不能为空")
    private String description;

    private List<String> images;

    private String video;

    private String backgroundMusic;
} 