package com.lovediary.controller;

import com.lovediary.dto.ApiResponse;
import com.lovediary.dto.DiaryDTO;
import com.lovediary.entity.Diary;
import com.lovediary.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/diaries")
@CrossOrigin(origins = "*")
public class DiaryController {

    @Autowired
    private DiaryService diaryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Diary>>> getAllDiaries() {
        try {
            List<Diary> diaries = diaryService.getAllDiaries();
            return ResponseEntity.ok(ApiResponse.success("获取日记列表成功", diaries));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取日记列表失败：" + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Diary>> getDiaryById(@PathVariable Long id) {
        try {
            Diary diary = diaryService.getDiaryById(id);
            return ResponseEntity.ok(ApiResponse.success("获取日记成功", diary));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取日记失败：" + e.getMessage()));
        }
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<ApiResponse<Diary>> getDiaryByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            Diary diary = diaryService.getDiaryByDate(date);
            return ResponseEntity.ok(ApiResponse.success("获取日记成功", diary));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取日记失败：" + e.getMessage()));
        }
    }

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<Diary>> getLatestDiary() {
        try {
            List<Diary> diaries = diaryService.getAllDiaries();
            if (!diaries.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success("获取最新日记成功", diaries.get(0)));
            } else {
                return ResponseEntity.ok(ApiResponse.error("没有日记数据"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取最新日记失败：" + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Diary>> createDiary(@Valid @RequestBody DiaryDTO diaryDTO) {
        try {
            if (diaryService.existsByDate(diaryDTO.getDate())) {
                return ResponseEntity.ok(ApiResponse.error("该日期已存在日记"));
            }
            Diary createdDiary = diaryService.createDiary(diaryDTO);
            return ResponseEntity.ok(ApiResponse.success("创建日记成功", createdDiary));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("创建日记失败：" + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Diary>> updateDiary(@PathVariable Long id, @Valid @RequestBody DiaryDTO diaryDTO) {
        try {
            Diary updatedDiary = diaryService.updateDiary(id, diaryDTO);
            return ResponseEntity.ok(ApiResponse.success("更新日记成功", updatedDiary));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("更新日记失败：" + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteDiary(@PathVariable Long id) {
        try {
            diaryService.deleteDiary(id);
            return ResponseEntity.ok(ApiResponse.success("删除日记成功", "日记已删除"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("删除日记失败：" + e.getMessage()));
        }
    }

    @GetMapping("/range")
    public ResponseEntity<ApiResponse<List<Diary>>> getDiariesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<Diary> diaries = diaryService.getDiariesByDateRange(startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("获取日期范围日记成功", diaries));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取日期范围日记失败：" + e.getMessage()));
        }
    }
} 