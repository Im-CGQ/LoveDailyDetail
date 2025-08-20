package com.lovediary.service.impl;

import com.lovediary.dto.DiaryDTO;
import com.lovediary.entity.Diary;
import com.lovediary.entity.User;
import com.lovediary.repository.DiaryRepository;
import com.lovediary.repository.UserRepository;
import com.lovediary.service.DiaryService;
import com.lovediary.service.OssService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;
    private final OssService ossService;

    @Override
    public List<Diary> getAllDiaries() {
        return diaryRepository.findAllByOrderByDateDesc();
    }

    @Override
    public List<Diary> getDiariesByUserId(Long userId) {
        return diaryRepository.findByUserIdOrderByDateDesc(userId);
    }

    @Override
    public Diary getDiaryById(Long id) {
        return diaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("日记不存在"));
    }

    @Override
    public Diary getDiaryByDateAndUserId(LocalDate date, Long userId) {
        return diaryRepository.findByDateAndUserId(date, userId)
                .orElseThrow(() -> new RuntimeException("该日期没有日记"));
    }

    @Override
    public Diary createDiary(DiaryDTO diaryDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
                
        if (diaryRepository.existsByDateAndUserId(diaryDTO.getDate(), userId)) {
            throw new RuntimeException("该日期已存在日记");
        }

        Diary diary = new Diary();
        BeanUtils.copyProperties(diaryDTO, diary);
        diary.setUser(user);
        return diaryRepository.save(diary);
    }

    @Override
    public Diary updateDiary(Long id, DiaryDTO diaryDTO) {
        Diary diary = getDiaryById(id);
        BeanUtils.copyProperties(diaryDTO, diary);
        return diaryRepository.save(diary);
    }

    @Override
    public void deleteDiary(Long id) {
        Diary diary = getDiaryById(id);
        diaryRepository.delete(diary);
    }

    @Override
    public List<Diary> getDiariesByYearAndMonth(int year, int month, Long userId) {
        return diaryRepository.findByYearAndMonthAndUserId(year, month, userId);
    }

    @Override
    public String uploadFile(MultipartFile file, String type) {
        return ossService.uploadFile(file, type);
    }

    @Override
    public List<Diary> getDiariesByDateRange(LocalDate startDate, LocalDate endDate, Long userId) {
        return diaryRepository.findByDateBetweenAndUserId(startDate, endDate, userId);
    }

    @Override
    public boolean existsByDateAndUserId(LocalDate date, Long userId) {
        return diaryRepository.existsByDateAndUserId(date, userId);
    }

    @Override
    public void initializeSampleData() {
        // 如果没有数据，创建一些示例数据
        if (diaryRepository.count() == 0) {
            createSampleDiaries();
        }
    }

    @Override
    public Page<Diary> getDiariesWithPagination(Pageable pageable) {
        return diaryRepository.findAll(pageable);
    }

    @Override
    public List<Diary> getRecentDiaries(int limit) {
        return diaryRepository.findAllByOrderByDateDesc().stream()
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public Page<Diary> getDiariesWithPaginationByUserId(Pageable pageable, Long userId) {
        return diaryRepository.findByUserIdOrderByDateDesc(userId, pageable);
    }
    
    @Override
    public List<Diary> getRecentDiariesByUserId(int limit, Long userId) {
        return diaryRepository.findByUserIdOrderByDateDesc(userId).stream()
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public List<Diary> getDiariesByDateRangeForAdmin(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findByDateBetween(startDate, endDate);
    }
    
    @Override
    public Diary createDiaryForAdmin(DiaryDTO diaryDTO) {
        // 管理员创建日记时，默认使用ID为1的用户（通常是管理员用户）
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("默认用户不存在"));
                
        if (diaryRepository.existsByDate(diaryDTO.getDate())) {
            throw new RuntimeException("该日期已存在日记");
        }

        Diary diary = new Diary();
        BeanUtils.copyProperties(diaryDTO, diary);
        diary.setUser(user);
        return diaryRepository.save(diary);
    }
    
    @Override
    public boolean existsByDate(LocalDate date) {
        return diaryRepository.existsByDate(date);
    }

    private void createSampleDiaries() {
        // 创建示例日记数据
        Diary diary1 = new Diary();
        diary1.setTitle("我们的第一次约会");
        diary1.setDate(LocalDate.of(2024, 1, 15));
        diary1.setDescription("今天是我们第一次约会，一起去看了电影，吃了火锅，度过了美好的一天。我们聊了很多，发现彼此有很多共同话题，感觉时间过得特别快。");
        diary1.setImages(Arrays.asList(
            "https://picsum.photos/400/300?random=1",
            "https://picsum.photos/400/300?random=2"
        ));
        diaryRepository.save(diary1);

        Diary diary2 = new Diary();
        diary2.setTitle("春天的野餐");
        diary2.setDate(LocalDate.of(2024, 3, 20));
        diary2.setDescription("今天天气很好，我们一起去公园野餐。准备了水果、三明治和饮料，在草地上铺了毯子，一边吃东西一边聊天，非常惬意。");
        diary2.setImages(Arrays.asList(
            "https://picsum.photos/400/300?random=3",
            "https://picsum.photos/400/300?random=4"
        ));
        diaryRepository.save(diary2);

        Diary diary3 = new Diary();
        diary3.setTitle("夏日海滩之旅");
        diary3.setDate(LocalDate.of(2024, 6, 15));
        diary3.setDescription("今天去了海边，阳光明媚，海风轻拂。我们一起在沙滩上散步，捡贝壳，拍照留念。海水很蓝，天空也很蓝，一切都那么美好。");
        diary3.setImages(Arrays.asList(
            "https://picsum.photos/400/300?random=5",
            "https://picsum.photos/400/300?random=6"
        ));
        diaryRepository.save(diary3);

        Diary diary4 = new Diary();
        diary4.setTitle("秋日枫叶之旅");
        diary4.setDate(LocalDate.of(2024, 10, 20));
        diary4.setDescription("秋天到了，枫叶红了。我们一起去山里看枫叶，漫山遍野的红叶美不胜收。拍了很多照片，留下了美好的回忆。");
        diary4.setImages(Arrays.asList(
            "https://picsum.photos/400/300?random=7",
            "https://picsum.photos/400/300?random=8"
        ));
        diaryRepository.save(diary4);
    }
} 