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
import com.lovediary.dto.SharedDiaryDTO;
import com.lovediary.dto.SharedLetterDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShareServiceImpl implements ShareService {
    
    private final ShareLinkRepository shareLinkRepository;
    private final DiaryRepository diaryRepository;
    private final LetterShareLinkRepository letterShareLinkRepository;
    private final LetterRepository letterRepository;
    
    @Override
    public ShareLink createShareLink(Long diaryId) {
        // 检查日记是否存在
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("日记不存在"));
        
        // 生成唯一的分享token
        String shareToken = generateShareToken();
        
        // 设置过期时间为3小时后
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(3);
        
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
        dto.setImages(diary.getImages());
        dto.setVideos(diary.getVideos());
        dto.setBackgroundMusic(diary.getBackgroundMusic());
        dto.setCreatedAt(diary.getCreatedAt());
        dto.setUpdatedAt(diary.getUpdatedAt());
        
        // 设置用户信息
        if (diary.getUser() != null) {
            dto.setUserId(diary.getUser().getId());
            dto.setUserName(diary.getUser().getUsername());
            dto.setUserDisplayName(diary.getUser().getDisplayName());
        }
        
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
    public LetterShareLink createLetterShareLink(Long letterId) {
        // 检查信件是否存在
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new RuntimeException("信件不存在"));
        
        // 生成唯一的分享token
        String shareToken = generateShareToken();
        
        // 设置过期时间为3小时后
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(3);
        
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
