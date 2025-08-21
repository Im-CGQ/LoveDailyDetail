package com.lovediary.dto;

import lombok.Data;

@Data
public class PartnerInvitationDTO {
    private Long id;
    private String fromUsername;
    private String fromDisplayName;
    private String toUsername;
    private String toDisplayName;
    private String status;
    private String createdAt;
}
