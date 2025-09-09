package com.lovediary.service.impl;

import com.lovediary.entity.Diary;
import com.lovediary.entity.ShareLink;
import com.lovediary.entity.Letter;
import com.lovediary.entity.LetterShareLink;
import com.lovediary.repository.DiaryRepository;
import com.lovediary.repository.ShareLinkRepository;
import com.lovediary.repository.LetterRepository;
import com.lovediary.repository.LetterShareLinkRepository;
import com.lovediary.service.ShareService;
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
public class ShareServiceImpl implements ShareService {
    
    private final ShareLinkRepository shareLinkRepository;
    private final DiaryRepository diaryRepository;
    private final LetterShareLinkRepository letterShareLinkRepository;
    private final LetterRepository letterRepository;
    private final SystemConfigService systemConfigService;
    
    @Override
    public ShareLink createShareLink(Long diaryId, Long userId) {
        // 检查日记是否存在
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("日记不存在"));
        
        // 生成唯一的分享token
        String shareToken = generateShareToken();
        
        // 从系统配置获取分享过期时间（分钟）
        Integer expireMinutes = userId != null ? 
            systemConfigService.getShareExpireMinutesByUserId(userId) : 60; // 默认60分钟
        // 设置过期时间
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(expireMinutes);
        
        // 创建分享链接
        ShareLink shareLink = new ShareLink();
        shareLink.setDiaryId(diaryId);
        shareLink.setShareToken(shareToken);
        shareLink.setExpiresAt(expiresAt);
        shareLink.setIsActive(true);
        
        return shareLinkRepository.save(shareLink);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SharedDiaryDTO getDiaryByShareToken(String shareToken) {
        ShareLink shareLink = shareLinkRepository.findByShareTokenAndIsActiveTrue(shareToken)
                .orElseThrow(() -> new RuntimeException("分享链接不存在或已过期"));
        
        // 检查是否过期
        if (shareLink.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("分享链接已过期");
        }
        
        Diary diary = diaryRepository.findById(shareLink.getDiaryId())
                .orElseThrow(() -> new RuntimeException("日记不存在"));
        
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
    public boolean isValidShareLink(String shareToken) {
        try {
            ShareLink shareLink = shareLinkRepository.findByShareTokenAndIsActiveTrue(shareToken)
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
    public void cleanupExpiredLinks() {
        List<ShareLink> expiredLinks = shareLinkRepository.findExpiredLinks(LocalDateTime.now());
        for (ShareLink link : expiredLinks) {
            link.setIsActive(false);
        }
        shareLinkRepository.saveAll(expiredLinks);
    }
    
    /**
     * 生成唯一的分享token
     */
    private String generateShareToken() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
    
    @Override
    public LetterShareLink createLetterShareLink(Long letterId, Long userId) {
        // 检查信件是否存在
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new RuntimeException("信件不存在"));
        
        // 生成唯一的分享token
        String shareToken = generateShareToken();
        
        // 从系统配置获取分享过期时间（分钟）
        Integer expireMinutes = userId != null ? 
            systemConfigService.getShareExpireMinutesByUserId(userId) : 60; // 默认60分钟
        // 设置过期时间
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(expireMinutes);
        
        // 创建分享链接
        LetterShareLink shareLink = new LetterShareLink();
        shareLink.setLetterId(letterId);
        shareLink.setShareToken(shareToken);
        shareLink.setExpiresAt(expiresAt);
        shareLink.setIsActive(true);
        
        return letterShareLinkRepository.save(shareLink);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SharedLetterDTO getLetterByShareToken(String shareToken) {
        LetterShareLink shareLink = letterShareLinkRepository.findByShareTokenAndIsActiveTrue(shareToken)
                .orElseThrow(() -> new RuntimeException("分享链接不存在或已过期"));
        
        // 检查是否过期
        if (shareLink.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("分享链接已过期");
        }
        
        Letter letter = letterRepository.findById(shareLink.getLetterId())
                .orElseThrow(() -> new RuntimeException("信件不存在"));
        
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
    public boolean isValidLetterShareLink(String shareToken) {
        try {
            LetterShareLink shareLink = letterShareLinkRepository.findByShareTokenAndIsActiveTrue(shareToken)
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
    public void cleanupExpiredLetterLinks() {
        List<LetterShareLink> expiredLinks = letterShareLinkRepository.findExpiredLinks(LocalDateTime.now());
        for (LetterShareLink link : expiredLinks) {
            link.setIsActive(false);
        }
        letterShareLinkRepository.saveAll(expiredLinks);
    }
}
