package com.lovediary.controller;

import com.lovediary.dto.ApiResponse;
import com.lovediary.dto.CreateLetterRequest;
import com.lovediary.dto.LetterDTO;
import com.lovediary.service.LetterService;
import com.lovediary.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/letters")
@CrossOrigin(origins = "*")
public class LetterController {

    @Autowired
    private LetterService letterService;

    @Autowired
    private JwtUtil jwtUtil;

    // 创建信件
    @PostMapping
    public ResponseEntity<ApiResponse<LetterDTO>> createLetter(
            @RequestBody CreateLetterRequest request,
            HttpServletRequest httpRequest) {
        try {
            System.out.println("LetterController - 创建信件请求");
            System.out.println("LetterController - 请求体: " + request.toString());
            Long userId = getUserIdFromRequest(httpRequest);
            System.out.println("LetterController - 创建信件，用户ID: " + userId);
            LetterDTO letter = letterService.createLetter(request, userId);
            System.out.println("LetterController - 信件创建成功，ID: " + letter.getId());
            return ResponseEntity.ok(ApiResponse.success(letter));
        } catch (Exception e) {
            System.out.println("LetterController - 创建信件失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 获取发送的信件列表
    @GetMapping("/sent")
    public ResponseEntity<ApiResponse<List<LetterDTO>>> getSentLetters(HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            System.out.println("LetterController - 获取发送信件，用户ID: " + userId);
            List<LetterDTO> letters = letterService.getSentLetters(userId);
            System.out.println("LetterController - 返回信件数量: " + letters.size());
            return ResponseEntity.ok(ApiResponse.success(letters));
        } catch (Exception e) {
            System.out.println("LetterController - 获取发送信件失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 获取接收的信件列表
    @GetMapping("/received")
    public ResponseEntity<ApiResponse<List<LetterDTO>>> getReceivedLetters(HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            List<LetterDTO> letters = letterService.getReceivedLetters(userId);
            return ResponseEntity.ok(ApiResponse.success(letters));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 获取已解锁的信件列表
    @GetMapping("/unlocked")
    public ResponseEntity<ApiResponse<List<LetterDTO>>> getUnlockedLetters(HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            List<LetterDTO> letters = letterService.getUnlockedLetters(userId);
            return ResponseEntity.ok(ApiResponse.success(letters));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 获取未解锁的信件列表
    @GetMapping("/locked")
    public ResponseEntity<ApiResponse<List<LetterDTO>>> getLockedLetters(HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            List<LetterDTO> letters = letterService.getLockedLetters(userId);
            return ResponseEntity.ok(ApiResponse.success(letters));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 获取信件详情
    @GetMapping("/{letterId}")
    public ResponseEntity<ApiResponse<LetterDTO>> getLetterById(
            @PathVariable Long letterId,
            HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            LetterDTO letter = letterService.getLetterById(letterId, userId);
            return ResponseEntity.ok(ApiResponse.success(letter));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 标记信件为已读
    @PutMapping("/{letterId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long letterId,
            HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            letterService.markAsRead(letterId, userId);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 更新信件
    @PutMapping("/{letterId}")
    public ResponseEntity<ApiResponse<LetterDTO>> updateLetter(
            @PathVariable Long letterId,
            @RequestBody CreateLetterRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            LetterDTO letter = letterService.updateLetter(letterId, request, userId);
            return ResponseEntity.ok(ApiResponse.success(letter));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 删除信件
    @DeleteMapping("/{letterId}")
    public ResponseEntity<ApiResponse<Void>> deleteLetter(
            @PathVariable Long letterId,
            HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            letterService.deleteLetter(letterId, userId);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        System.out.println("LetterController - Authorization header: " + token);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            System.out.println("LetterController - Token after Bearer: " + token);
            Long userId = jwtUtil.getUserIdFromToken(token);
            System.out.println("LetterController - Extracted userId: " + userId);
            return userId;
        }
        throw new RuntimeException("未找到有效的认证令牌");
    }
}
