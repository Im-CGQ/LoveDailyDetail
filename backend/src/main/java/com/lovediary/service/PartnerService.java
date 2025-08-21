package com.lovediary.service;

import com.lovediary.dto.ApiResponse;
import com.lovediary.dto.PartnerInfoDTO;

public interface PartnerService {
    
    /**
     * 获取用户的伴侣信息
     */
    PartnerInfoDTO getPartnerInfo(Long userId);
    
    /**
     * 邀请伴侣
     */
    ApiResponse invitePartner(Long fromUserId, String targetUsername);
    
    /**
     * 接受邀请
     */
    ApiResponse acceptInvitation(Long userId, Long invitationId);
    
    /**
     * 拒绝邀请
     */
    ApiResponse rejectInvitation(Long userId, Long invitationId);
    
    /**
     * 解除伴侣关系
     */
    ApiResponse unbindPartner(Long userId);
    
    /**
     * 取消邀请
     */
    ApiResponse cancelInvitation(Long userId, Long invitationId);
}
