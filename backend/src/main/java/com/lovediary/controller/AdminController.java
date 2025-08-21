package com.lovediary.controller;

import com.lovediary.dto.ApiResponse;
import com.lovediary.dto.DiaryDTO;
import com.lovediary.entity.Diary;
import com.lovediary.entity.User;
import com.lovediary.service.DiaryService;
import com.lovediary.service.OssSignatureService;
import com.lovediary.service.UserService;
import com.lovediary.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private DiaryService diaryService;
    
    @Autowired
    private OssSignatureService ossSignatureService;
    
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
     * 获取后台统计数据
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats(
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            
            List<Diary> allDiaries = diaryService.getDiariesByUserId(userId);
            
            // 计算本月日记数量
            LocalDate now = LocalDate.now();
            YearMonth currentMonth = YearMonth.from(now);
            LocalDate startOfMonth = currentMonth.atDay(1);
            LocalDate endOfMonth = currentMonth.atEndOfMonth();
            
            List<Diary> thisMonthDiaries = diaryService.getDiariesByDateRange(startOfMonth, endOfMonth, userId);
            
            // 计算今年日记数量
            LocalDate startOfYear = LocalDate.of(now.getYear(), 1, 1);
            LocalDate endOfYear = LocalDate.of(now.getYear(), 12, 31);
            List<Diary> thisYearDiaries = diaryService.getDiariesByDateRange(startOfYear, endOfYear, userId);
            
            // 统计图片和视频数量
            long totalImages = allDiaries.stream()
                    .filter(diary -> diary.getImages() != null)
                    .mapToLong(diary -> diary.getImages().size())
                    .sum();
            
            long totalVideos = allDiaries.stream()
                    .filter(diary -> diary.getVideos() != null)
                    .mapToLong(diary -> diary.getVideos().size())
                    .sum();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalDiaries", allDiaries.size());
            stats.put("thisMonthDiaries", thisMonthDiaries.size());
            stats.put("thisYearDiaries", thisYearDiaries.size());
            stats.put("totalImages", totalImages);
            stats.put("totalVideos", totalVideos);
            
            return ResponseEntity.ok(ApiResponse.success("获取统计数据成功", stats));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取统计数据失败：" + e.getMessage()));
        }
    }

    /**
     * 分页获取日记列表
     */
    @GetMapping("/diaries")
    public ResponseEntity<ApiResponse<Page<Diary>>> getDiariesWithPagination(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "date"));
            Page<Diary> diaries = diaryService.getDiariesWithPaginationByUserId(pageable, userId);
            return ResponseEntity.ok(ApiResponse.success("获取日记列表成功", diaries));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取日记列表失败：" + e.getMessage()));
        }
    }

    /**
     * 获取最近日记
     */
    @GetMapping("/diaries/recent")
    public ResponseEntity<ApiResponse<List<Diary>>> getRecentDiaries(
            @RequestParam(defaultValue = "5") int limit,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            
            List<Diary> diaries = diaryService.getRecentDiariesByUserId(limit, userId);
            return ResponseEntity.ok(ApiResponse.success("获取最近日记成功", diaries));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取最近日记失败：" + e.getMessage()));
        }
    }

    /**
     * 根据ID获取日记详情
     */
    @GetMapping("/diaries/{id}")
    public ResponseEntity<ApiResponse<Diary>> getDiaryById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            
            Diary diary = diaryService.getDiaryById(id);
            if (diary.getUser().getId() != userId) {
                return ResponseEntity.ok(ApiResponse.error("无权查看此日记"));
            }
            
            return ResponseEntity.ok(ApiResponse.success("获取日记详情成功", diary));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取日记详情失败：" + e.getMessage()));
        }
    }

    /**
     * 创建日记
     */
    @PostMapping("/diaries")
    public ResponseEntity<ApiResponse<Diary>> createDiary(
            @Valid @RequestBody DiaryDTO diaryDTO,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            
            if (diaryService.existsByDateAndUserId(diaryDTO.getDate(), userId)) {
                return ResponseEntity.ok(ApiResponse.error("该日期已存在日记"));
            }
            Diary createdDiary = diaryService.createDiary(diaryDTO, userId);
            return ResponseEntity.ok(ApiResponse.success("创建日记成功", createdDiary));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("创建日记失败：" + e.getMessage()));
        }
    }

    /**
     * 更新日记
     */
    @PutMapping("/diaries/{id}")
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

    /**
     * 删除日记
     */
    @DeleteMapping("/diaries/{id}")
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

    /**
     * 获取OSS上传签名
     */
    @GetMapping("/oss/signature")
    public ResponseEntity<ApiResponse<Map<String, String>>> getOssSignature(
            @RequestParam("fileName") String fileName,
            @RequestParam("fileType") String fileType) {
        try {
            Map<String, String> signature = ossSignatureService.getUploadSignature(fileName, fileType);
            return ResponseEntity.ok(ApiResponse.success("获取上传签名成功", signature));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取上传签名失败：" + e.getMessage()));
        }
    }

    /**
     * 文件上传（保留原有接口用于兼容）
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type) {
        try {
            String fileUrl = diaryService.uploadFile(file, type);
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            return ResponseEntity.ok(ApiResponse.success("文件上传成功", result));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("文件上传失败：" + e.getMessage()));
        }
    }
} 