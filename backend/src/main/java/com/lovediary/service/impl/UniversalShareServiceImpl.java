package com.lovediary.service.impl;

import com.lovediary.entity.Diary;
import com.lovediary.entity.Letter;
import com.lovediary.entity.UniversalShareLink;
import com.lovediary.repository.DiaryRepository;
import com.lovediary.repository.LetterRepository;
import com.lovediary.repository.UniversalShareLinkRepository;
import com.lovediary.service.UniversalShareService;
import com.lovediary.service.SystemConfigService;
import com.lovediary.dto.SharedDiaryDTO;
import com.lovediary.dto.SharedLetterDTO;
import com.lovediary.dto.ImageInfoDTO;
import com.lovediary.dto.VideoInfoDTO;
import com.lovediary.dto.DiaryBackgroundMusicDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UniversalShareServiceImpl implements UniversalShareService {
    
    private final UniversalShareLinkRepository universalShareLinkRepository;
    private final DiaryRepository diaryRepository;
    private final LetterRepository letterRepository;
    private final SystemConfigService systemConfigService;
    
    @Override
    public UniversalShareLink createShareLink(Long targetId, UniversalShareLink.ShareType shareType, Long userId) {
        // 验证目标对象是否存在
        validateTargetExists(targetId, shareType);
        
        // 生成唯一的分享token
        String shareToken = generateShareToken();
        
        // 从系统配置获取分享过期时间（分钟）
        Integer expireMinutes;
        if (userId != null) {
            expireMinutes = systemConfigService.getShareExpireMinutesByUserId(userId);
        } else {
            expireMinutes = 60; // 默认60分钟
        }
        
        // 设置过期时间
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(expireMinutes);
        
        // 创建分享链接
        UniversalShareLink shareLink = new UniversalShareLink();
        shareLink.setTargetId(targetId);
        shareLink.setShareType(shareType);
        shareLink.setShareToken(shareToken);
        shareLink.setExpiresAt(expiresAt);
        shareLink.setIsActive(true);
        shareLink.setViewCount(0);
        
        return universalShareLinkRepository.save(shareLink);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Object getTargetByShareToken(String shareToken, UniversalShareLink.ShareType shareType) {
        UniversalShareLink shareLink = universalShareLinkRepository
                .findByShareTokenAndShareTypeAndIsActiveTrue(shareToken, shareType)
                .orElseThrow(() -> new RuntimeException("分享链接不存在或已过期"));
        
        // 检查是否过期
        if (shareLink.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("分享链接已过期");
        }
        
        // 增加查看次数
        shareLink.setViewCount(shareLink.getViewCount() + 1);
        universalShareLinkRepository.save(shareLink);
        
        // 根据类型返回相应的对象
        switch (shareType) {
            case DIARY:
                return getDiaryByShareToken(shareToken);
            case LETTER:
                return getLetterByShareToken(shareToken);
            default:
                throw new RuntimeException("不支持的分享类型: " + shareType);
        }
    }
    
    @Override
    public boolean isValidShareLink(String shareToken, UniversalShareLink.ShareType shareType) {
        try {
            UniversalShareLink shareLink = universalShareLinkRepository
                    .findByShareTokenAndShareTypeAndIsActiveTrue(shareToken, shareType)
                    .orElse(null);
            
            if (shareLink == null) {
                return false;
            }
            
            return shareLink.getExpiresAt().isAfter(LocalDateTime.now());
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public void cleanupExpiredLinks(UniversalShareLink.ShareType shareType) {
        List<UniversalShareLink> expiredLinks;
        if (shareType != null) {
            expiredLinks = universalShareLinkRepository.findExpiredLinksByType(shareType, LocalDateTime.now());
        } else {
            expiredLinks = universalShareLinkRepository.findExpiredLinks(LocalDateTime.now());
        }
        
        for (UniversalShareLink link : expiredLinks) {
            link.setIsActive(false);
        }
        universalShareLinkRepository.saveAll(expiredLinks);
    }
    
    // 兼容旧API的方法实现
    
    @Override
    public UniversalShareLink createDiaryShareLink(Long diaryId, Long userId) {
        return createShareLink(diaryId, UniversalShareLink.ShareType.DIARY, userId);
    }
    
    @Override
    public UniversalShareLink createLetterShareLink(Long letterId, Long userId) {
        return createShareLink(letterId, UniversalShareLink.ShareType.LETTER, userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SharedDiaryDTO getDiaryByShareToken(String shareToken) {
        UniversalShareLink shareLink = universalShareLinkRepository
                .findByShareTokenAndShareTypeAndIsActiveTrue(shareToken, UniversalShareLink.ShareType.DIARY)
                .orElseThrow(() -> new RuntimeException("分享链接不存在或已过期"));
        
        // 检查是否过期
        if (shareLink.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("分享链接已过期");
        }
        
        Diary diary = diaryRepository.findById(shareLink.getTargetId())
                .orElseThrow(() -> new RuntimeException("日记不存在"));
        
        // 增加查看次数
        shareLink.setViewCount(shareLink.getViewCount() + 1);
        universalShareLinkRepository.save(shareLink);
        
        // 转换为DTO，避免懒加载问题
        SharedDiaryDTO dto = new SharedDiaryDTO();
        dto.setId(diary.getId());
        dto.setTitle(diary.getTitle());
        dto.setDescription(diary.getDescription());
        dto.setDate(diary.getDate());
        
        // 转换图片信息
        if (diary.getImages() != null) {
            List<ImageInfoDTO> imageDTOs = diary.getImages().stream()
                .map(image -> {
                    ImageInfoDTO imageDTO = new ImageInfoDTO();
                    imageDTO.setImageUrl(image.getImageUrl());
                    imageDTO.setFileName(image.getFileName());
                    imageDTO.setWidth(image.getWidth());
                    imageDTO.setHeight(image.getHeight());
                    return imageDTO;
                })
                .collect(Collectors.toList());
            dto.setImages(imageDTOs);
        }
        
        // 转换视频信息
        if (diary.getVideos() != null) {
            List<VideoInfoDTO> videoDTOs = diary.getVideos().stream()
                .map(video -> {
                    VideoInfoDTO videoDTO = new VideoInfoDTO();
                    videoDTO.setVideoUrl(video.getVideoUrl());
                    videoDTO.setFileName(video.getFileName());
                    videoDTO.setWidth(video.getWidth());
                    videoDTO.setHeight(video.getHeight());
                    return videoDTO;
                })
                .collect(Collectors.toList());
            dto.setVideos(videoDTOs);
        }
        
        // 转换背景音乐信息
        if (diary.getBackgroundMusic() != null) {
            List<DiaryBackgroundMusicDTO> musicDTOs = diary.getBackgroundMusic().stream()
                .map(music -> {
                    DiaryBackgroundMusicDTO musicDTO = new DiaryBackgroundMusicDTO();
                    musicDTO.setMusicUrl(music.getMusicUrl());
                    musicDTO.setFileName(music.getFileName());
                    musicDTO.setTitle(music.getTitle());
                    musicDTO.setArtist(music.getArtist());
                    musicDTO.setDuration(music.getDuration());
                    return musicDTO;
                })
                .collect(Collectors.toList());
            dto.setBackgroundMusic(musicDTOs);
        }
        
        dto.setCreatedAt(diary.getCreatedAt());
        dto.setUpdatedAt(diary.getUpdatedAt());
        
        // 设置用户信息
        if (diary.getUser() != null) {
            dto.setUserId(diary.getUser().getId());
            dto.setUserName(diary.getUser().getUsername());
            dto.setUserDisplayName(diary.getUser().getDisplayName());
        }
        
        // 设置分享链接过期时间
        dto.setExpiresAt(shareLink.getExpiresAt());
        
        return dto;
    }
    
    @Override
    @Transactional(readOnly = true)
    public SharedLetterDTO getLetterByShareToken(String shareToken) {
        UniversalShareLink shareLink = universalShareLinkRepository
                .findByShareTokenAndShareTypeAndIsActiveTrue(shareToken, UniversalShareLink.ShareType.LETTER)
                .orElseThrow(() -> new RuntimeException("分享链接不存在或已过期"));
        
        // 检查是否过期
        if (shareLink.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("分享链接已过期");
        }
        
        Letter letter = letterRepository.findById(shareLink.getTargetId())
                .orElseThrow(() -> new RuntimeException("信件不存在"));
        
        // 增加查看次数
        shareLink.setViewCount(shareLink.getViewCount() + 1);
        universalShareLinkRepository.save(shareLink);
        
        // 转换为DTO，避免懒加载问题
        SharedLetterDTO dto = new SharedLetterDTO();
        dto.setId(letter.getId());
        dto.setTitle(letter.getTitle());
        dto.setContent(letter.getContent());
        dto.setUnlockTime(letter.getUnlockTime());
        dto.setCreatedAt(letter.getCreatedAt());
        dto.setUpdatedAt(letter.getUpdatedAt());
        dto.setIsRead(letter.getIsRead());
        
        // 设置发送者信息
        if (letter.getSender() != null) {
            dto.setSenderId(letter.getSender().getId());
            dto.setSenderName(letter.getSender().getUsername());
            dto.setSenderDisplayName(letter.getSender().getDisplayName());
        }
        
        // 设置接收者信息
        if (letter.getReceiver() != null) {
            dto.setReceiverId(letter.getReceiver().getId());
            dto.setReceiverName(letter.getReceiver().getUsername());
            dto.setReceiverDisplayName(letter.getReceiver().getDisplayName());
        }
        
        // 设置分享链接过期时间
        dto.setExpiresAt(shareLink.getExpiresAt());
        
        return dto;
    }
    
    @Override
    public boolean isValidDiaryShareLink(String shareToken) {
        return isValidShareLink(shareToken, UniversalShareLink.ShareType.DIARY);
    }
    
    @Override
    public boolean isValidLetterShareLink(String shareToken) {
        return isValidShareLink(shareToken, UniversalShareLink.ShareType.LETTER);
    }
    
    @Override
    public void cleanupExpiredDiaryLinks() {
        cleanupExpiredLinks(UniversalShareLink.ShareType.DIARY);
    }
    
    @Override
    public void cleanupExpiredLetterLinks() {
        cleanupExpiredLinks(UniversalShareLink.ShareType.LETTER);
    }
    
    /**
     * 验证目标对象是否存在
     */
    private void validateTargetExists(Long targetId, UniversalShareLink.ShareType shareType) {
        switch (shareType) {
            case DIARY:
                diaryRepository.findById(targetId)
                        .orElseThrow(() -> new RuntimeException("日记不存在"));
                break;
            case LETTER:
                letterRepository.findById(targetId)
                        .orElseThrow(() -> new RuntimeException("信件不存在"));
                break;
            default:
                throw new RuntimeException("不支持的分享类型: " + shareType);
        }
    }
    
    /**
     * 生成唯一的分享token
     */
    private String generateShareToken() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
