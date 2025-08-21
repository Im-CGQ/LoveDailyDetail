package com.lovediary.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class ChatRecordDTO {

    @NotBlank(message = "聊天类型不能为空")
    private String chatType;

    @NotNull(message = "聊天时长不能为空")
    @Min(value = 1, message = "聊天时长必须大于0")
    private Integer durationMinutes;

    @NotNull(message = "聊天日期不能为空")
    private LocalDate date;

    private String description;

    private String customType;
}
