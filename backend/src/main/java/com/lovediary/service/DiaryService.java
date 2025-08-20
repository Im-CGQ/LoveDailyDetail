package com.lovediary.service;

import com.lovediary.dto.DiaryDTO;
import com.lovediary.entity.Diary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface DiaryService {

    List<Diary> getAllDiaries();

    Diary getDiaryById(Long id);

    Diary getDiaryByDate(LocalDate date);

    Diary createDiary(DiaryDTO diaryDTO);

    Diary updateDiary(Long id, DiaryDTO diaryDTO);

    void deleteDiary(Long id);

    List<Diary> getDiariesByYearAndMonth(int year, int month);

    String uploadFile(MultipartFile file, String type);

    List<Diary> getDiariesByDateRange(LocalDate startDate, LocalDate endDate);

    boolean existsByDate(LocalDate date);

    void initializeSampleData();

    // 新增的admin相关方法
    Page<Diary> getDiariesWithPagination(Pageable pageable);

    List<Diary> getRecentDiaries(int limit);
} 