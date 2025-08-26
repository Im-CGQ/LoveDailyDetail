package com.lovediary.service.impl;

import com.lovediary.dto.DiaryDTO;
import com.lovediary.dto.ImageInfoDTO;
import com.lovediary.dto.VideoInfoDTO;
import com.lovediary.dto.DiaryBackgroundMusicDTO;
import com.lovediary.entity.Diary;
import com.lovediary.entity.ImageInfo;
import com.lovediary.entity.User;
import com.lovediary.entity.VideoInfo;
import com.lovediary.entity.DiaryBackgroundMusic;
import com.lovediary.repository.DiaryRepository;
import com.lovediary.repository.ImageInfoRepository;
import com.lovediary.repository.UserRepository;
import com.lovediary.repository.VideoInfoRepository;
import com.lovediary.repository.DiaryBackgroundMusicRepository;
import com.lovediary.service.DiaryService;
import com.lovediary.service.OssService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;
    private final OssService ossService;
    private final DiaryBackgroundMusicRepository diaryBackgroundMusicRepository;

    @Override
    public List<Diary> getAllDiaries() {
        return diaryRepository.findAllByOrderByDateDesc();
    }

    @Override
    public List<Diary> getDiariesByUserId(Long userId) {
        // 获取用户信息
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return diaryRepository.findByUserIdOrderByDateDesc(userId);
        }
        
        User user = userOpt.get();
        
        // 如果用户有伴侣，返回用户和伴侣的日记
        if (user.getPartnerId() != null) {
            return diaryRepository.findByUserIdOrUserIdOrderByDateDesc(userId, user.getPartnerId());
        }
        
        // 否则只返回用户的日记
        return diaryRepository.findByUserIdOrderByDateDesc(userId);
    }

    @Override
    public List<Diary> getViewableDiariesByUserId(Long userId) {
        // 直接使用新的查询方法，基于partner_id字段
        return diaryRepository.findViewableDiariesByUserIdWithPartnerId(userId);
    }

    @Override
    public List<Diary> getOwnDiariesByUserId(Long userId) {
        // 只返回用户自己创建的日记
        return diaryRepository.findOwnDiariesByUserId(userId);
    }

    @Override
    public Diary getDiaryById(Long id) {
        return diaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("日记不存在"));
    }

    @Override
    public Optional<Diary> getDiaryByDateAndUserId(LocalDate date, Long userId) {
        return diaryRepository.findByDateAndUserId(date, userId);
    }

    @Override
    public Optional<Diary> getViewableDiaryByDateAndUserId(LocalDate date, Long userId) {
        return diaryRepository.findViewableDiaryByDateAndUserId(date, userId);
    }

    @Override
    public List<Diary> getViewableDiariesByDateAndUserId(LocalDate date, Long userId) {
        return diaryRepository.findViewableDiariesByDateAndUserId(date, userId);
    }

    @Override
    public Diary createDiary(DiaryDTO diaryDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
                
        // 移除日期唯一性检查，允许同一天创建多条日记

        Diary diary = new Diary();
        diary.setTitle(diaryDTO.getTitle());
        diary.setDate(diaryDTO.getDate());
        diary.setDescription(diaryDTO.getDescription());
        diary.setUser(user);
        
        // 如果用户有伴侣，设置partner_id字段
        if (user.getPartnerId() != null) {
            User partner = userRepository.findById(user.getPartnerId())
                    .orElseThrow(() -> new RuntimeException("伴侣信息不存在"));
            diary.setPartner(partner);
        }
        
        // 先保存日记，获取ID
        diary = diaryRepository.save(diary);
        
        // 处理背景音乐信息
        if (diaryDTO.getBackgroundMusic() != null && !diaryDTO.getBackgroundMusic().isEmpty()) {
            List<DiaryBackgroundMusic> backgroundMusicList = new ArrayList<>();
            for (DiaryBackgroundMusicDTO musicDTO : diaryDTO.getBackgroundMusic()) {
                DiaryBackgroundMusic backgroundMusic = new DiaryBackgroundMusic();
                backgroundMusic.setDiaryId(diary.getId());
                backgroundMusic.setMusicUrl(musicDTO.getMusicUrl());
                backgroundMusic.setFileName(musicDTO.getFileName());
                backgroundMusic.setTitle(musicDTO.getTitle());
                backgroundMusic.setArtist(musicDTO.getArtist());
                backgroundMusic.setDuration(musicDTO.getDuration());
                backgroundMusicList.add(backgroundMusic);
            }
            diary.setBackgroundMusic(backgroundMusicList);
        }
        
        // 处理图片信息
        if (diaryDTO.getImages() != null && !diaryDTO.getImages().isEmpty()) {
            List<ImageInfo> imageInfos = new ArrayList<>();
            for (ImageInfoDTO imageDTO : diaryDTO.getImages()) {
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.setDiaryId(diary.getId());
                imageInfo.setImageUrl(imageDTO.getImageUrl());
                imageInfo.setFileName(imageDTO.getFileName());
                imageInfo.setWidth(imageDTO.getWidth());
                imageInfo.setHeight(imageDTO.getHeight());
                imageInfos.add(imageInfo);
            }
            diary.setImages(imageInfos);
        }
        
        // 处理视频信息
        if (diaryDTO.getVideos() != null && !diaryDTO.getVideos().isEmpty()) {
            List<VideoInfo> videoInfos = new ArrayList<>();
            for (VideoInfoDTO videoDTO : diaryDTO.getVideos()) {
                VideoInfo videoInfo = new VideoInfo();
                videoInfo.setDiaryId(diary.getId());
                videoInfo.setVideoUrl(videoDTO.getVideoUrl());
                videoInfo.setFileName(videoDTO.getFileName());
                videoInfo.setWidth(videoDTO.getWidth());
                videoInfo.setHeight(videoDTO.getHeight());
                videoInfos.add(videoInfo);
            }
            diary.setVideos(videoInfos);
        }
        
        return diaryRepository.save(diary);
    }

    @Override
    public Diary updateDiary(Long id, DiaryDTO diaryDTO) {
        Diary diary = getDiaryById(id);
        
        // 更新字段，保持ID和用户信息不变
        diary.setTitle(diaryDTO.getTitle());
        diary.setDescription(diaryDTO.getDescription());
        diary.setDate(diaryDTO.getDate());
        
        // 处理背景音乐信息
        if (diaryDTO.getBackgroundMusic() != null && !diaryDTO.getBackgroundMusic().isEmpty()) {
            List<DiaryBackgroundMusic> backgroundMusicList = new ArrayList<>();
            for (DiaryBackgroundMusicDTO musicDTO : diaryDTO.getBackgroundMusic()) {
                DiaryBackgroundMusic backgroundMusic = new DiaryBackgroundMusic();
                backgroundMusic.setDiaryId(diary.getId());
                backgroundMusic.setMusicUrl(musicDTO.getMusicUrl());
                backgroundMusic.setFileName(musicDTO.getFileName());
                backgroundMusic.setTitle(musicDTO.getTitle());
                backgroundMusic.setArtist(musicDTO.getArtist());
                backgroundMusic.setDuration(musicDTO.getDuration());
                backgroundMusicList.add(backgroundMusic);
            }
            diary.setBackgroundMusic(backgroundMusicList);
        } else {
            diary.setBackgroundMusic(null);
        }
        
        // 清除现有的图片和视频信息
        if (diary.getImages() != null) {
            diary.getImages().clear();
        }
        if (diary.getVideos() != null) {
            diary.getVideos().clear();
        }
        
        // 处理图片信息
        if (diaryDTO.getImages() != null && !diaryDTO.getImages().isEmpty()) {
            List<ImageInfo> imageInfos = new ArrayList<>();
            for (ImageInfoDTO imageDTO : diaryDTO.getImages()) {
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.setDiaryId(diary.getId());
                imageInfo.setImageUrl(imageDTO.getImageUrl());
                imageInfo.setFileName(imageDTO.getFileName());
                imageInfo.setWidth(imageDTO.getWidth());
                imageInfo.setHeight(imageDTO.getHeight());
                imageInfos.add(imageInfo);
            }
            diary.setImages(imageInfos);
        }
        
        // 处理视频信息
        if (diaryDTO.getVideos() != null && !diaryDTO.getVideos().isEmpty()) {
            List<VideoInfo> videoInfos = new ArrayList<>();
            for (VideoInfoDTO videoDTO : diaryDTO.getVideos()) {
                VideoInfo videoInfo = new VideoInfo();
                videoInfo.setDiaryId(diary.getId());
                videoInfo.setVideoUrl(videoDTO.getVideoUrl());
                videoInfo.setFileName(videoDTO.getFileName());
                videoInfo.setWidth(videoDTO.getWidth());
                videoInfo.setHeight(videoDTO.getHeight());
                videoInfos.add(videoInfo);
            }
            diary.setVideos(videoInfos);
        }
        
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
    public List<Diary> getViewableDiariesByDateRange(LocalDate startDate, LocalDate endDate, Long userId) {
        return diaryRepository.findViewableDiariesByDateRange(startDate, endDate, userId);
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
    public Page<Diary> getOwnDiariesWithPaginationByUserId(Pageable pageable, Long userId) {
        return diaryRepository.findOwnDiariesByUserIdOrderByDateDesc(userId, pageable);
    }
    
    @Override
    public List<Diary> getOwnRecentDiariesByUserId(int limit, Long userId) {
        return diaryRepository.findOwnDiariesByUserId(userId).stream()
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
        diary.setTitle(diaryDTO.getTitle());
        diary.setDate(diaryDTO.getDate());
        diary.setDescription(diaryDTO.getDescription());
        diary.setUser(user);
        
        // 处理背景音乐信息
        if (diaryDTO.getBackgroundMusic() != null && !diaryDTO.getBackgroundMusic().isEmpty()) {
            List<DiaryBackgroundMusic> backgroundMusicList = new ArrayList<>();
            for (DiaryBackgroundMusicDTO musicDTO : diaryDTO.getBackgroundMusic()) {
                DiaryBackgroundMusic backgroundMusic = new DiaryBackgroundMusic();
                backgroundMusic.setDiaryId(diary.getId());
                backgroundMusic.setMusicUrl(musicDTO.getMusicUrl());
                backgroundMusic.setFileName(musicDTO.getFileName());
                backgroundMusic.setTitle(musicDTO.getTitle());
                backgroundMusic.setArtist(musicDTO.getArtist());
                backgroundMusic.setDuration(musicDTO.getDuration());
                backgroundMusicList.add(backgroundMusic);
            }
            diary.setBackgroundMusic(backgroundMusicList);
        }
        
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
        diaryRepository.save(diary1);

        Diary diary2 = new Diary();
        diary2.setTitle("春天的野餐");
        diary2.setDate(LocalDate.of(2024, 3, 20));
        diary2.setDescription("今天天气很好，我们一起去公园野餐。准备了水果、三明治和饮料，在草地上铺了毯子，一边吃东西一边聊天，非常惬意。");
        diaryRepository.save(diary2);

        Diary diary3 = new Diary();
        diary3.setTitle("夏日海滩之旅");
        diary3.setDate(LocalDate.of(2024, 6, 15));
        diary3.setDescription("今天去了海边，阳光明媚，海风轻拂。我们一起在沙滩上散步，捡贝壳，拍照留念。海水很蓝，天空也很蓝，一切都那么美好。");
        diaryRepository.save(diary3);

        Diary diary4 = new Diary();
        diary4.setTitle("秋日枫叶之旅");
        diary4.setDate(LocalDate.of(2024, 10, 20));
        diary4.setDescription("秋天到了，枫叶红了。我们一起去山里看枫叶，漫山遍野的红叶美不胜收。拍了很多照片，留下了美好的回忆。");
        diaryRepository.save(diary4);
    }
} 