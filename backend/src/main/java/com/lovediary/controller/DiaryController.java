package com.lovediary.controller;

import com.lovediary.dto.ApiResponse;
import com.lovediary.dto.DiaryDTO;
import com.lovediary.entity.Diary;
import com.lovediary.entity.User;
import com.lovediary.service.DiaryService;
import com.lovediary.service.UserService;
import com.lovediary.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/diaries")
@CrossOrigin(origins = "*")
public class DiaryController {

    @Autowired
    private DiaryService diaryService;
    
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
    public ResponseEntity<ApiResponse<List<Diary>>> getAllDiaries(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            // 使用新的方法获取用户可以查看的日记（包括伴侣的）
            List<Diary> diaries = diaryService.getViewableDiariesByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success("获取日记列表成功", diaries));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取日记列表失败：" + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Diary>> getDiaryById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            
            Diary diary = diaryService.getDiaryById(id);
            if (diary == null) {
                return ResponseEntity.ok(ApiResponse.error("日记不存在"));
            }
            
            // 检查权限：用户只能查看自己的日记或伴侣的日记
            Long diaryUserId = diary.getUser().getId();
            if (!diaryUserId.equals(userId)) {
                // 检查是否是伴侣关系
                User currentUser = userService.findById(userId).orElse(null);
                if (currentUser == null || !diaryUserId.equals(currentUser.getPartnerId())) {
                    return ResponseEntity.ok(ApiResponse.error("无权查看此日记"));
                }
            }
            
            return ResponseEntity.ok(ApiResponse.success("获取日记成功", diary));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取日记失败：" + e.getMessage()));
        }
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<ApiResponse<List<Diary>>> getDiaryByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            List<Diary> diaries = diaryService.getViewableDiariesByDateAndUserId(date, userId);
            if (!diaries.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success("获取日记成功", diaries));
            } else {
                return ResponseEntity.ok(ApiResponse.success("该日期暂无日记", new ArrayList<>()));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取日记失败：" + e.getMessage()));
        }
    }

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<Diary>> getLatestDiary(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            List<Diary> diaries = diaryService.getViewableDiariesByUserId(userId);
            if (!diaries.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success("获取最新日记成功", diaries.get(0)));
            } else {
                return ResponseEntity.ok(ApiResponse.success("暂无日记数据", null));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取最新日记失败：" + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Diary>> createDiary(@Valid @RequestBody DiaryDTO diaryDTO,
                                                         @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            // 移除日期唯一性检查，允许同一天创建多条日记
            Diary createdDiary = diaryService.createDiary(diaryDTO, userId);
            return ResponseEntity.ok(ApiResponse.success("创建日记成功", createdDiary));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("创建日记失败：" + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Diary>> updateDiary(
            @PathVariable Long id, 
            @Valid @RequestBody DiaryDTO diaryDTO,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            
            Diary existingDiary = diaryService.getDiaryById(id);
            if (existingDiary.getUser().getId() != userId) {
                return ResponseEntity.ok(ApiResponse.error("无权更新此日记"));
            }
            
            Diary updatedDiary = diaryService.updateDiary(id, diaryDTO);
            return ResponseEntity.ok(ApiResponse.success("更新日记成功", updatedDiary));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("更新日记失败：" + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteDiary(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            
            Diary existingDiary = diaryService.getDiaryById(id);
            if (existingDiary.getUser().getId() != userId) {
                return ResponseEntity.ok(ApiResponse.error("无权删除此日记"));
            }
            
            diaryService.deleteDiary(id);
            return ResponseEntity.ok(ApiResponse.success("删除日记成功", "日记已删除"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("删除日记失败：" + e.getMessage()));
        }
    }

    @GetMapping("/range")
    public ResponseEntity<ApiResponse<List<Diary>>> getDiariesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            List<Diary> diaries = diaryService.getViewableDiariesByDateRange(startDate, endDate, userId);
            return ResponseEntity.ok(ApiResponse.success("获取日期范围日记成功", diaries));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取日期范围日记失败：" + e.getMessage()));
        }
    }
} 