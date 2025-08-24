package com.lovediary.controller;

import com.lovediary.dto.ApiResponse;
import com.lovediary.dto.ChatRecordDTO;
import com.lovediary.entity.ChatRecord;
import com.lovediary.entity.User;
import com.lovediary.service.ChatRecordService;
import com.lovediary.service.UserService;
import com.lovediary.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin/chat-records")
@CrossOrigin(origins = "*")
public class AdminChatRecordController {

    @Autowired
    private ChatRecordService chatRecordService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        try {
            String actualToken = token.substring(7);
            String username = jwtUtil.extractUsername(actualToken);
            User user = userService.findByUsername(username).orElse(null);
            return user != null ? user.getId() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 分页获取聊天记录列表（只显示当前用户的）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ChatRecord>>> getChatRecordsWithPagination(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "date"));
            Page<ChatRecord> chatRecords = chatRecordService.getOwnChatRecordsWithPaginationByUserId(pageable, userId);
            return ResponseEntity.ok(ApiResponse.success("获取聊天记录列表成功", chatRecords));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取聊天记录列表失败：" + e.getMessage()));
        }
    }

    /**
     * 根据ID获取聊天记录详情（只显示当前用户的）
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ChatRecord>> getChatRecordById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            
            ChatRecord chatRecord = chatRecordService.getChatRecordById(id);
            if (chatRecord.getUser().getId() != userId) {
                return ResponseEntity.ok(ApiResponse.error("无权查看此聊天记录"));
            }
            
            return ResponseEntity.ok(ApiResponse.success("获取聊天记录详情成功", chatRecord));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取聊天记录详情失败：" + e.getMessage()));
        }
    }

    /**
     * 创建聊天记录
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ChatRecord>> createChatRecord(
            @Valid @RequestBody ChatRecordDTO chatRecordDTO,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            
            ChatRecord createdChatRecord = chatRecordService.createChatRecord(chatRecordDTO, userId);
            return ResponseEntity.ok(ApiResponse.success("创建聊天记录成功", createdChatRecord));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("创建聊天记录失败：" + e.getMessage()));
        }
    }

    /**
     * 更新聊天记录（只更新当前用户的）
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ChatRecord>> updateChatRecord(
            @PathVariable Long id, 
            @Valid @RequestBody ChatRecordDTO chatRecordDTO,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            
            ChatRecord existingChatRecord = chatRecordService.getChatRecordById(id);
            if (existingChatRecord.getUser().getId() != userId) {
                return ResponseEntity.ok(ApiResponse.error("无权更新此聊天记录"));
            }
            
            ChatRecord updatedChatRecord = chatRecordService.updateChatRecord(id, chatRecordDTO);
            return ResponseEntity.ok(ApiResponse.success("更新聊天记录成功", updatedChatRecord));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("更新聊天记录失败：" + e.getMessage()));
        }
    }

    /**
     * 删除聊天记录（只删除当前用户的）
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteChatRecord(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            
            ChatRecord existingChatRecord = chatRecordService.getChatRecordById(id);
            if (existingChatRecord.getUser().getId() != userId) {
                return ResponseEntity.ok(ApiResponse.error("无权删除此聊天记录"));
            }
            
            chatRecordService.deleteChatRecord(id);
            return ResponseEntity.ok(ApiResponse.success("删除聊天记录成功", "聊天记录已删除"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("删除聊天记录失败：" + e.getMessage()));
        }
    }
}
