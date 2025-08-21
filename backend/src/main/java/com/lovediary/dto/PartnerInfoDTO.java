package com.lovediary.dto;

import lombok.Data;

@Data
public class PartnerInfoDTO {
    private Long partnerId;
    private String partnerUsername;
    private String partnerDisplayName;
    private Boolean hasPartner;
    private Boolean hasPendingInvitation;
    private PartnerInvitationDTO pendingInvitation;
    private Boolean hasSentInvitation;
    private PartnerInvitationDTO sentInvitation;
}
