package com.lovediary.dto;

import lombok.Data;

@Data
public class SystemConfigDTO {
    private Long id;
    private String configKey;
    private String configValue;
    private String configType;
    private String description;
    private Long userId;
}

