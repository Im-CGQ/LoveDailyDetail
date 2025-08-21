package com.lovediary.controller;

import com.lovediary.dto.ApiResponse;
import com.lovediary.dto.InvitePartnerRequest;
import com.lovediary.dto.PartnerInfoDTO;
import com.lovediary.service.PartnerService;
import com.lovediary.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/partner")
@CrossOrigin(origins = "*")
public class PartnerController {

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取伴侣信息
     */
    @GetMapping("/info")
    public ApiResponse getPartnerInfo(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ApiResponse.error("未授权访问");
            }
            
            token = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            PartnerInfoDTO partnerInfo = partnerService.getPartnerInfo(userId);
            return ApiResponse.success(partnerInfo);
        } catch (Exception e) {
            return ApiResponse.error("获取伴侣信息失败: " + e.getMessage());
        }
    }

    /**
     * 邀请伴侣
     */
    @PostMapping("/invite")
    public ApiResponse invitePartner(@RequestBody InvitePartnerRequest request, HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ApiResponse.error("未授权访问");
            }
            
            token = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            if (request.getTargetUsername() == null || request.getTargetUsername().trim().isEmpty()) {
                return ApiResponse.error("请输入用户名");
            }
            
            return partnerService.invitePartner(userId, request.getTargetUsername().trim());
        } catch (Exception e) {
            return ApiResponse.error("邀请失败: " + e.getMessage());
        }
    }

    /**
     * 接受邀请
     */
    @PostMapping("/accept/{invitationId}")
    public ApiResponse acceptInvitation(@PathVariable Long invitationId, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ApiResponse.error("未授权访问");
            }
            
            token = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            return partnerService.acceptInvitation(userId, invitationId);
        } catch (Exception e) {
            return ApiResponse.error("接受邀请失败: " + e.getMessage());
        }
    }

    /**
     * 拒绝邀请
     */
    @PostMapping("/reject/{invitationId}")
    public ApiResponse rejectInvitation(@PathVariable Long invitationId, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ApiResponse.error("未授权访问");
            }
            
            token = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            return partnerService.rejectInvitation(userId, invitationId);
        } catch (Exception e) {
            return ApiResponse.error("拒绝邀请失败: " + e.getMessage());
        }
    }

    /**
     * 解除伴侣关系
     */
    @PostMapping("/unbind")
    public ApiResponse unbindPartner(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ApiResponse.error("未授权访问");
            }
            
            token = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            return partnerService.unbindPartner(userId);
        } catch (Exception e) {
            return ApiResponse.error("解除关系失败: " + e.getMessage());
        }
    }

    /**
     * 取消邀请
     */
    @PostMapping("/cancel/{invitationId}")
    public ApiResponse cancelInvitation(@PathVariable Long invitationId, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ApiResponse.error("未授权访问");
            }
            
            token = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            return partnerService.cancelInvitation(userId, invitationId);
        } catch (Exception e) {
            return ApiResponse.error("取消邀请失败: " + e.getMessage());
        }
    }
}
