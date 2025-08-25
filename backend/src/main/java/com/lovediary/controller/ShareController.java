package com.lovediary.controller;

import com.lovediary.dto.ApiResponse;
import com.lovediary.dto.SharedDiaryDTO;
import com.lovediary.dto.SharedLetterDTO;
import com.lovediary.entity.ShareLink;
import com.lovediary.entity.LetterShareLink;
import com.lovediary.service.ShareService;
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
    
    /**
     * 创建分享链接
     */
    @PostMapping("/create/{diaryId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> createShareLink(@PathVariable Long diaryId) {
        try {
            ShareLink shareLink = shareService.createShareLink(diaryId);
            
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
    public ResponseEntity<ApiResponse<Map<String, String>>> createLetterShareLink(@PathVariable Long letterId) {
        try {
            LetterShareLink shareLink = shareService.createLetterShareLink(letterId);
            
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
