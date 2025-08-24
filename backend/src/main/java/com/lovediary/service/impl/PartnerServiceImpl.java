package com.lovediary.service.impl;

import com.lovediary.dto.ApiResponse;
import com.lovediary.dto.PartnerInfoDTO;
import com.lovediary.dto.PartnerInvitationDTO;
import com.lovediary.entity.PartnerInvitation;
import com.lovediary.entity.User;
import com.lovediary.repository.PartnerInvitationRepository;
import com.lovediary.repository.UserRepository;
import com.lovediary.service.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class PartnerServiceImpl implements PartnerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PartnerInvitationRepository partnerInvitationRepository;

    @Override
    public PartnerInfoDTO getPartnerInfo(Long userId) {
        PartnerInfoDTO partnerInfo = new PartnerInfoDTO();
        
        // 获取用户信息
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            partnerInfo.setHasPartner(false);
            partnerInfo.setHasPendingInvitation(false);
            return partnerInfo;
        }
        
        User user = userOpt.get();
        
        // 检查是否有伴侣
        if (user.getPartnerId() != null) {
            Optional<User> partnerOpt = userRepository.findById(user.getPartnerId());
            if (partnerOpt.isPresent()) {
                User partner = partnerOpt.get();
                partnerInfo.setHasPartner(true);
                partnerInfo.setPartnerId(partner.getId());
                partnerInfo.setPartnerUsername(partner.getUsername());
                partnerInfo.setPartnerDisplayName(partner.getDisplayName());
            }
        } else {
            partnerInfo.setHasPartner(false);
        }
        
        // 检查是否有待处理的邀请
        List<PartnerInvitation> pendingInvitations = partnerInvitationRepository.findPendingInvitationsByToUserId(userId);
        if (!pendingInvitations.isEmpty()) {
            partnerInfo.setHasPendingInvitation(true);
            PartnerInvitation invitation = pendingInvitations.get(0);
            PartnerInvitationDTO invitationDTO = convertToDTO(invitation);
            partnerInfo.setPendingInvitation(invitationDTO);
        } else {
            partnerInfo.setHasPendingInvitation(false);
        }
        
        // 检查是否有发送的邀请
        List<PartnerInvitation> sentInvitations = partnerInvitationRepository.findPendingInvitationsByFromUserId(userId);
        if (!sentInvitations.isEmpty()) {
            partnerInfo.setHasSentInvitation(true);
            PartnerInvitation sentInvitation = sentInvitations.get(0);
            PartnerInvitationDTO sentInvitationDTO = convertToDTO(sentInvitation);
            partnerInfo.setSentInvitation(sentInvitationDTO);
        } else {
            partnerInfo.setHasSentInvitation(false);
        }
        
        return partnerInfo;
    }

    @Override
    @Transactional
    public ApiResponse invitePartner(Long fromUserId, String targetUsername) {
        // 检查目标用户是否存在
        Optional<User> targetUserOpt = userRepository.findByUsername(targetUsername);
        if (!targetUserOpt.isPresent()) {
            return ApiResponse.error("用户不存在");
        }
        
        User targetUser = targetUserOpt.get();
        
        // 不能邀请自己
        if (fromUserId.equals(targetUser.getId())) {
            return ApiResponse.error("不能邀请自己成为伴侣");
        }
        
        // 检查发起邀请的用户是否有伴侣
        Optional<User> fromUserOpt = userRepository.findById(fromUserId);
        if (!fromUserOpt.isPresent()) {
            return ApiResponse.error("用户不存在");
        }
        
        User fromUser = fromUserOpt.get();
        if (fromUser.getPartnerId() != null) {
            return ApiResponse.error("您已经有伴侣了");
        }
        
        // 检查目标用户是否有伴侣
        if (targetUser.getPartnerId() != null) {
            return ApiResponse.error("对方已经有伴侣了");
        }
        
        // 检查是否已经存在邀请记录（包括被拒绝的）
        Optional<PartnerInvitation> existingInvitation = partnerInvitationRepository
                .findByFromUserIdAndToUserId(fromUserId, targetUser.getId());
        
        if (existingInvitation.isPresent()) {
            PartnerInvitation invitation = existingInvitation.get();
            
            // 如果邀请状态是PENDING，说明已经发送过邀请
            if (invitation.getStatus() == PartnerInvitation.Status.PENDING) {
                return ApiResponse.error("已经发送过邀请了，请等待对方回复");
            }
            
            // 如果邀请状态是REJECTED，重新激活邀请
            if (invitation.getStatus() == PartnerInvitation.Status.REJECTED) {
                invitation.setStatus(PartnerInvitation.Status.PENDING);
                partnerInvitationRepository.save(invitation);
                return ApiResponse.success("邀请重新发送成功");
            }
            
            // 如果邀请状态是ACCEPTED，不应该到达这里，因为已经有伴侣关系
            return ApiResponse.error("邀请状态异常");
        }
        
        // 检查是否已经向其他用户发送过邀请
        List<PartnerInvitation> existingInvitations = partnerInvitationRepository
                .findPendingInvitationsByFromUserId(fromUserId);
        if (!existingInvitations.isEmpty()) {
            return ApiResponse.error("您已经向其他用户发送过邀请了，请等待回复或取消后再发送新邀请");
        }
        
        // 创建新的邀请
        PartnerInvitation invitation = new PartnerInvitation();
        invitation.setFromUserId(fromUserId);
        invitation.setToUserId(targetUser.getId());
        invitation.setStatus(PartnerInvitation.Status.PENDING);
        
        partnerInvitationRepository.save(invitation);
        
        return ApiResponse.success("邀请发送成功");
    }

    @Override
    @Transactional
    public ApiResponse acceptInvitation(Long userId, Long invitationId) {
        // 检查邀请是否存在
        Optional<PartnerInvitation> invitationOpt = partnerInvitationRepository.findById(invitationId);
        if (!invitationOpt.isPresent()) {
            return ApiResponse.error("邀请不存在");
        }
        
        PartnerInvitation invitation = invitationOpt.get();
        
        // 检查邀请是否属于当前用户
        if (!invitation.getToUserId().equals(userId)) {
            return ApiResponse.error("无权操作此邀请");
        }
        
        // 检查邀请状态
        if (invitation.getStatus() != PartnerInvitation.Status.PENDING) {
            return ApiResponse.error("邀请已被处理");
        }
        
        // 获取用户信息
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<User> fromUserOpt = userRepository.findById(invitation.getFromUserId());
        
        if (!userOpt.isPresent() || !fromUserOpt.isPresent()) {
            return ApiResponse.error("用户不存在");
        }
        
        User user = userOpt.get();
        User fromUser = fromUserOpt.get();
        
        // 检查双方是否已经有伴侣
        if (user.getPartnerId() != null || fromUser.getPartnerId() != null) {
            return ApiResponse.error("双方中有人已经有伴侣了");
        }
        
        // 建立伴侣关系
        user.setPartnerId(fromUser.getId());
        fromUser.setPartnerId(user.getId());
        
        userRepository.save(user);
        userRepository.save(fromUser);
        
        // 更新邀请状态
        invitation.setStatus(PartnerInvitation.Status.ACCEPTED);
        partnerInvitationRepository.save(invitation);
        
        return ApiResponse.success("伴侣关系建立成功");
    }

    @Override
    @Transactional
    public ApiResponse rejectInvitation(Long userId, Long invitationId) {
        // 检查邀请是否存在
        Optional<PartnerInvitation> invitationOpt = partnerInvitationRepository.findById(invitationId);
        if (!invitationOpt.isPresent()) {
            return ApiResponse.error("邀请不存在");
        }
        
        PartnerInvitation invitation = invitationOpt.get();
        
        // 检查邀请是否属于当前用户
        if (!invitation.getToUserId().equals(userId)) {
            return ApiResponse.error("无权操作此邀请");
        }
        
        // 更新邀请状态
        invitation.setStatus(PartnerInvitation.Status.REJECTED);
        partnerInvitationRepository.save(invitation);
        
        return ApiResponse.success("已拒绝邀请");
    }

    @Override
    @Transactional
    public ApiResponse unbindPartner(Long userId) {
        // 获取用户信息
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return ApiResponse.error("用户不存在");
        }
        
        User user = userOpt.get();
        
        // 检查是否有伴侣
        if (user.getPartnerId() == null) {
            return ApiResponse.error("您还没有伴侣");
        }
        
        // 获取伴侣信息
        Optional<User> partnerOpt = userRepository.findById(user.getPartnerId());
        if (!partnerOpt.isPresent()) {
            return ApiResponse.error("伴侣信息异常");
        }
        
        User partner = partnerOpt.get();
        
        // 解除伴侣关系
        user.setPartnerId(null);
        partner.setPartnerId(null);
        
        userRepository.save(user);
        userRepository.save(partner);
        
        // 清理伴侣邀请表中的相关记录
        // 删除所有与这两个用户相关的邀请记录（无论状态如何）
        List<PartnerInvitation> invitationsToDelete = partnerInvitationRepository.findByFromUserIdOrToUserId(userId, partner.getId());
        if (!invitationsToDelete.isEmpty()) {
            partnerInvitationRepository.deleteAll(invitationsToDelete);
        }
        
        return ApiResponse.success("伴侣关系已解除");
    }

    @Override
    @Transactional
    public ApiResponse cancelInvitation(Long userId, Long invitationId) {
        // 检查邀请是否存在
        Optional<PartnerInvitation> invitationOpt = partnerInvitationRepository.findById(invitationId);
        if (!invitationOpt.isPresent()) {
            return ApiResponse.error("邀请不存在");
        }
        
        PartnerInvitation invitation = invitationOpt.get();
        
        // 检查邀请是否属于当前用户
        if (!invitation.getFromUserId().equals(userId)) {
            return ApiResponse.error("无权操作此邀请");
        }
        
        // 检查邀请状态
        if (invitation.getStatus() != PartnerInvitation.Status.PENDING) {
            return ApiResponse.error("邀请已被处理，无法取消");
        }
        
        // 删除邀请
        partnerInvitationRepository.delete(invitation);
        
        return ApiResponse.success("邀请已取消");
    }

    private PartnerInvitationDTO convertToDTO(PartnerInvitation invitation) {
        PartnerInvitationDTO dto = new PartnerInvitationDTO();
        dto.setId(invitation.getId());
        dto.setStatus(invitation.getStatus().name());
        dto.setCreatedAt(invitation.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        // 获取发送邀请的用户信息
        Optional<User> fromUserOpt = userRepository.findById(invitation.getFromUserId());
        if (fromUserOpt.isPresent()) {
            User fromUser = fromUserOpt.get();
            dto.setFromUsername(fromUser.getUsername());
            dto.setFromDisplayName(fromUser.getDisplayName());
        }
        
        // 获取接收邀请的用户信息
        Optional<User> toUserOpt = userRepository.findById(invitation.getToUserId());
        if (toUserOpt.isPresent()) {
            User toUser = toUserOpt.get();
            dto.setToUsername(toUser.getUsername());
            dto.setToDisplayName(toUser.getDisplayName());
        }
        
        return dto;
    }
}
