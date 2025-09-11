package com.lovediary.controller;

import com.lovediary.dto.ApiResponse;
import com.lovediary.dto.SharedDiaryDTO;
import com.lovediary.dto.SharedLetterDTO;
import com.lovediary.dto.SharedMovieDTO;
import com.lovediary.entity.UniversalShareLink;
import com.lovediary.service.UniversalShareService;
import com.lovediary.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/share")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UniversalShareController {
    
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
            System.out.println("UniversalShareController - 获取用户ID失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 测试端点
     */
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> test() {
        return ResponseEntity.ok(ApiResponse.success("UniversalShareController 工作正常", "OK"));
    }
    
    /**
     * 创建通用分享链接
     */
    @PostMapping("/create/{shareType}/{targetId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> createShareLink(
            @PathVariable String shareType,
            @PathVariable Long targetId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            UniversalShareLink.ShareType type = UniversalShareLink.ShareType.valueOf(shareType.toUpperCase());
            Long userId = getCurrentUserId(token);
            UniversalShareLink shareLink = universalShareService.createShareLink(targetId, type, userId);
            
            // 构建分享链接
            String shareUrl = "/api/share/" + shareType.toLowerCase() + "/" + shareLink.getShareToken();
            
            Map<String, String> result = new HashMap<>();
            result.put("shareUrl", shareUrl);
            result.put("shareToken", shareLink.getShareToken());
            result.put("expiresAt", shareLink.getExpiresAt().toString());
            result.put("shareType", shareLink.getShareType().name());
            
            return ResponseEntity.ok(ApiResponse.success("创建分享链接成功", result));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("创建分享链接失败：" + e.getMessage()));
        }
    }
    
    /**
     * 通过分享链接查看内容（无需登录）
     */
    @GetMapping("/{shareType}/{shareToken}")
    public ResponseEntity<ApiResponse<Object>> viewSharedContent(
            @PathVariable String shareType,
            @PathVariable String shareToken) {
        try {
            UniversalShareLink.ShareType type = UniversalShareLink.ShareType.valueOf(shareType.toUpperCase());
            Object content = universalShareService.getTargetByShareToken(shareToken, type);
            return ResponseEntity.ok(ApiResponse.success("获取分享内容成功", content));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取分享内容失败：" + e.getMessage()));
        }
    }
    
    /**
     * 验证分享链接是否有效
     */
    @GetMapping("/validate/{shareType}/{shareToken}")
    public ResponseEntity<ApiResponse<Boolean>> validateShareLink(
            @PathVariable String shareType,
            @PathVariable String shareToken) {
        try {
            UniversalShareLink.ShareType type = UniversalShareLink.ShareType.valueOf(shareType.toUpperCase());
            boolean isValid = universalShareService.isValidShareLink(shareToken, type);
            return ResponseEntity.ok(ApiResponse.success("验证分享链接成功", isValid));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("验证分享链接失败：" + e.getMessage()));
        }
    }
    
    /**
     * 清理过期的分享链接
     */
    @PostMapping("/cleanup")
    public ResponseEntity<ApiResponse<String>> cleanupExpiredLinks(
            @RequestParam(required = false) String shareType) {
        try {
            if (shareType != null && !shareType.isEmpty()) {
                UniversalShareLink.ShareType type = UniversalShareLink.ShareType.valueOf(shareType.toUpperCase());
                universalShareService.cleanupExpiredLinks(type);
                return ResponseEntity.ok(ApiResponse.success("清理过期分享链接成功", "已清理类型: " + type.getDescription()));
            } else {
                universalShareService.cleanupExpiredLinks(null);
                return ResponseEntity.ok(ApiResponse.success("清理过期分享链接成功", "已清理所有类型"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("清理过期分享链接失败：" + e.getMessage()));
        }
    }
    
    // 兼容旧API的方法
    
    /**
     * 创建日记分享链接（兼容旧API）
     */
    @PostMapping("/diary/create/{diaryId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> createDiaryShareLink(
            @PathVariable Long diaryId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            UniversalShareLink shareLink = universalShareService.createDiaryShareLink(diaryId, userId);
            
            // 构建分享链接
            String shareUrl = "/share/diary/" + shareLink.getShareToken();
            
            Map<String, String> result = new HashMap<>();
            result.put("shareUrl", shareUrl);
            result.put("shareToken", shareLink.getShareToken());
            result.put("expiresAt", shareLink.getExpiresAt().toString());
            
            return ResponseEntity.ok(ApiResponse.success("创建日记分享链接成功", result));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("创建日记分享链接失败：" + e.getMessage()));
        }
    }
    
    /**
     * 通过分享链接查看日记（兼容旧API）
     */
    @GetMapping("/diary/{shareToken}")
    public ResponseEntity<ApiResponse<SharedDiaryDTO>> viewSharedDiary(@PathVariable String shareToken) {
        try {
            SharedDiaryDTO diary = universalShareService.getDiaryByShareToken(shareToken);
            return ResponseEntity.ok(ApiResponse.success("获取分享日记成功", diary));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取分享日记失败：" + e.getMessage()));
        }
    }
    
    /**
     * 创建信件分享链接（兼容旧API）
     */
    @PostMapping("/letter/create/{letterId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> createLetterShareLink(
            @PathVariable Long letterId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            UniversalShareLink shareLink = universalShareService.createLetterShareLink(letterId, userId);
            
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
     * 通过分享链接查看信件（兼容旧API）
     */
    @GetMapping("/letter/{shareToken}")
    public ResponseEntity<ApiResponse<SharedLetterDTO>> viewSharedLetter(@PathVariable String shareToken) {
        try {
            SharedLetterDTO letter = universalShareService.getLetterByShareToken(shareToken);
            return ResponseEntity.ok(ApiResponse.success("获取分享信件成功", letter));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取分享信件失败：" + e.getMessage()));
        }
    }
    
    /**
     * 创建电影分享链接（兼容旧API）
     */
    @PostMapping("/movie/create/{movieId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> createMovieShareLink(
            @PathVariable Long movieId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            UniversalShareLink shareLink = universalShareService.createMovieShareLink(movieId, userId);
            
            // 构建分享链接
            String shareUrl = "/share/movie/" + shareLink.getShareToken();
            
            Map<String, String> result = new HashMap<>();
            result.put("shareUrl", shareUrl);
            result.put("shareToken", shareLink.getShareToken());
            result.put("expiresAt", shareLink.getExpiresAt().toString());
            
            return ResponseEntity.ok(ApiResponse.success("创建电影分享链接成功", result));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("创建电影分享链接失败：" + e.getMessage()));
        }
    }
    
    /**
     * 通过分享链接查看电影（兼容旧API）
     */
    @GetMapping("/movie/{shareToken}")
    public ResponseEntity<ApiResponse<SharedMovieDTO>> viewSharedMovie(@PathVariable String shareToken) {
        try {
            SharedMovieDTO movie = universalShareService.getMovieByShareToken(shareToken);
            return ResponseEntity.ok(ApiResponse.success("获取分享电影成功", movie));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取分享电影失败：" + e.getMessage()));
        }
    }
}
