package com.lovediary.service;

import com.lovediary.dto.DiaryDTO;
import com.lovediary.entity.Diary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryService {

    List<Diary> getAllDiaries();

    List<Diary> getDiariesByUserId(Long userId);
    
    // 获取用户可以查看的日记（包括自己的和伴侣的）
    List<Diary> getViewableDiariesByUserId(Long userId);
    
    // 获取用户自己创建的日记（用于后台管理）
    List<Diary> getOwnDiariesByUserId(Long userId);

    Diary getDiaryById(Long id);

    Optional<Diary> getDiaryByDateAndUserId(LocalDate date, Long userId);
    
    // 按日期获取用户可以查看的日记
    Optional<Diary> getViewableDiaryByDateAndUserId(LocalDate date, Long userId);
    
    // 按日期获取用户可以查看的日记列表
    List<Diary> getViewableDiariesByDateAndUserId(LocalDate date, Long userId);

    Diary createDiary(DiaryDTO diaryDTO, Long userId);

    Diary updateDiary(Long id, DiaryDTO diaryDTO);

    void deleteDiary(Long id);

    List<Diary> getDiariesByYearAndMonth(int year, int month, Long userId);

    String uploadFile(MultipartFile file, String type);

    List<Diary> getDiariesByDateRange(LocalDate startDate, LocalDate endDate, Long userId);
    
    // 按日期范围获取用户可以查看的日记
    List<Diary> getViewableDiariesByDateRange(LocalDate startDate, LocalDate endDate, Long userId);

    boolean existsByDateAndUserId(LocalDate date, Long userId);

    void initializeSampleData();

    // 新增的admin相关方法
    Page<Diary> getDiariesWithPagination(Pageable pageable);

    List<Diary> getRecentDiaries(int limit);
    
    // 用户相关方法
    Page<Diary> getDiariesWithPaginationByUserId(Pageable pageable, Long userId);
    
    List<Diary> getRecentDiariesByUserId(int limit, Long userId);
    
    // 分页获取用户自己创建的日记（用于后台管理）
    Page<Diary> getOwnDiariesWithPaginationByUserId(Pageable pageable, Long userId);
    
    // 获取用户自己创建的最近日记（用于后台管理）
    List<Diary> getOwnRecentDiariesByUserId(int limit, Long userId);
    
    // 管理员专用方法（保留用于兼容）
    List<Diary> getDiariesByDateRangeForAdmin(LocalDate startDate, LocalDate endDate);
    
    Diary createDiaryForAdmin(DiaryDTO diaryDTO);
    
    boolean existsByDate(LocalDate date);
} 