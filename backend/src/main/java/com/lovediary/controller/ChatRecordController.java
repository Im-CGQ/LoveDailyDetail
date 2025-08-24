package com.lovediary.controller;

import com.lovediary.dto.ApiResponse;
import com.lovediary.dto.ChatRecordDTO;
import com.lovediary.entity.ChatRecord;
import com.lovediary.entity.User;
import com.lovediary.service.ChatRecordService;
import com.lovediary.service.UserService;
import com.lovediary.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/chat-records")
@CrossOrigin(origins = "*")
public class ChatRecordController {

    @Autowired
    private ChatRecordService chatRecordService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;

    // 获取当前用户ID的辅助方法
    private Long getCurrentUserId(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtUtil.extractUsername(token);
            User user = userService.findByUsername(username).orElse(null);
            return user != null ? user.getId() : null;
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ChatRecord>>> getAllChatRecords(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            List<ChatRecord> chatRecords = chatRecordService.getChatRecordsByUserIdAndPartner(userId);
            return ResponseEntity.ok(ApiResponse.success("获取聊天记录列表成功", chatRecords));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取聊天记录列表失败：" + e.getMessage()));
        }
    }

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
            if (chatRecord == null) {
                return ResponseEntity.ok(ApiResponse.error("聊天记录不存在"));
            }
            
            // 检查权限：用户只能查看自己的聊天记录或伴侣的聊天记录
            Long chatRecordUserId = chatRecord.getUser().getId();
            if (!chatRecordUserId.equals(userId)) {
                // 检查是否是伴侣关系
                User currentUser = userService.findById(userId).orElse(null);
                if (currentUser == null || !chatRecordUserId.equals(currentUser.getPartnerId())) {
                    return ResponseEntity.ok(ApiResponse.error("无权查看此聊天记录"));
                }
            }
            
            return ResponseEntity.ok(ApiResponse.success("获取聊天记录成功", chatRecord));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取聊天记录失败：" + e.getMessage()));
        }
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<ApiResponse<List<ChatRecord>>> getChatRecordsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            List<ChatRecord> chatRecords = chatRecordService.getChatRecordsByDateAndUserIdAndPartner(date, userId);
            return ResponseEntity.ok(ApiResponse.success("获取聊天记录成功", chatRecords));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取聊天记录失败：" + e.getMessage()));
        }
    }

    @GetMapping("/total-duration")
    public ResponseEntity<ApiResponse<Integer>> getTotalDuration(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            Integer totalDuration = chatRecordService.getTotalDurationByUserIdAndPartner(userId);
            return ResponseEntity.ok(ApiResponse.success("获取总聊天时长成功", totalDuration));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取总聊天时长失败：" + e.getMessage()));
        }
    }

    @GetMapping("/duration-by-type")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getDurationByChatType(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            Map<String, Integer> durationByType = chatRecordService.getDurationByChatTypeAndPartner(userId);
            return ResponseEntity.ok(ApiResponse.success("获取聊天类型时长统计成功", durationByType));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取聊天类型时长统计失败：" + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ChatRecord>> createChatRecord(@Valid @RequestBody ChatRecordDTO chatRecordDTO,
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

    @GetMapping("/range")
    public ResponseEntity<ApiResponse<List<ChatRecord>>> getChatRecordsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            List<ChatRecord> chatRecords = chatRecordService.getChatRecordsByDateRangeAndPartner(startDate, endDate, userId);
            return ResponseEntity.ok(ApiResponse.success("获取日期范围聊天记录成功", chatRecords));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取日期范围聊天记录失败：" + e.getMessage()));
        }
    }
}
