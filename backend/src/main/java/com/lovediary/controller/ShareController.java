package com.lovediary.controller;

import com.lovediary.dto.ApiResponse;
import com.lovediary.dto.SharedDiaryDTO;
import com.lovediary.dto.SharedLetterDTO;
import com.lovediary.entity.ShareLink;
import com.lovediary.entity.LetterShareLink;
import com.lovediary.service.ShareService;
import com.lovediary.service.UniversalShareService;
import com.lovediary.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/share")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ShareController {
    
    private final ShareService shareService;
    private final UniversalShareService universalShareService;
    private final JwtUtil jwtUtil;
    
    private Long getCurrentUserId(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        try {
            // 处理 Bearer 前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            return jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            System.out.println("ShareController - 获取用户ID失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 创建分享链接
     */
    @PostMapping("/create/{diaryId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> createShareLink(
            @PathVariable Long diaryId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            ShareLink shareLink = shareService.createShareLink(diaryId, userId);
            
            // 构建分享链接
            String shareUrl = "/share/diary/" + shareLink.getShareToken();
            
            Map<String, String> result = new HashMap<>();
            result.put("shareUrl", shareUrl);
            result.put("shareToken", shareLink.getShareToken());
            result.put("expiresAt", shareLink.getExpiresAt().toString());
            
            return ResponseEntity.ok(ApiResponse.success("创建分享链接成功", result));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("创建分享链接失败：" + e.getMessage()));
        }
    }
    
    /**
     * 通过分享链接查看日记（无需登录）
     */
    @GetMapping("/diary/{shareToken}")
    public ResponseEntity<ApiResponse<SharedDiaryDTO>> viewSharedDiary(@PathVariable String shareToken) {
        try {
            SharedDiaryDTO diary = shareService.getDiaryByShareToken(shareToken);
            return ResponseEntity.ok(ApiResponse.success("获取分享日记成功", diary));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取分享日记失败：" + e.getMessage()));
        }
    }
    
    /**
     * 验证分享链接是否有效
     */
    @GetMapping("/validate/{shareToken}")
    public ResponseEntity<ApiResponse<Boolean>> validateShareLink(@PathVariable String shareToken) {
        try {
            boolean isValid = shareService.isValidShareLink(shareToken);
            return ResponseEntity.ok(ApiResponse.success("验证分享链接成功", isValid));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("验证分享链接失败：" + e.getMessage()));
        }
    }
    
    /**
     * 创建信件分享链接
     */
    @PostMapping("/letter/create/{letterId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> createLetterShareLink(
            @PathVariable Long letterId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            LetterShareLink shareLink = shareService.createLetterShareLink(letterId, userId);
            
            // 构建分享链接
            String shareUrl = "/share/letter/" + shareLink.getShareToken();
            
            Map<String, String> result = new HashMap<>();
            result.put("shareUrl", shareUrl);
            result.put("shareToken", shareLink.getShareToken());
            result.put("expiresAt", shareLink.getExpiresAt().toString());
            
            return ResponseEntity.ok(ApiResponse.success("创建信件分享链接成功", result));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("创建信件分享链接失败：" + e.getMessage()));
        }
    }
    
    /**
     * 通过分享链接查看信件（无需登录）
     */
    @GetMapping("/letter/{shareToken}")
    public ResponseEntity<ApiResponse<SharedLetterDTO>> viewSharedLetter(@PathVariable String shareToken) {
        try {
            SharedLetterDTO letter = shareService.getLetterByShareToken(shareToken);
            return ResponseEntity.ok(ApiResponse.success("获取分享信件成功", letter));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取分享信件失败：" + e.getMessage()));
        }
    }
}
