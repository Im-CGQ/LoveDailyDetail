package com.lovediary.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateLetterRequest {
    private String title;
    private String content;
    private LocalDateTime unlockTime;
    private Long receiverId;
}
