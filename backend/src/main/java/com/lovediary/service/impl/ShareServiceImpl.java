package com.lovediary.service.impl;

import com.lovediary.entity.Diary;
import com.lovediary.entity.ShareLink;
import com.lovediary.entity.Letter;
import com.lovediary.entity.LetterShareLink;
import com.lovediary.entity.UniversalShareLink;
import com.lovediary.repository.DiaryRepository;
import com.lovediary.repository.ShareLinkRepository;
import com.lovediary.repository.LetterRepository;
import com.lovediary.repository.LetterShareLinkRepository;
import com.lovediary.service.ShareService;
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
public class ShareServiceImpl implements ShareService {
    
    private final ShareLinkRepository shareLinkRepository;
    private final DiaryRepository diaryRepository;
    private final LetterShareLinkRepository letterShareLinkRepository;
    private final LetterRepository letterRepository;
    private final SystemConfigService systemConfigService;
    private final UniversalShareService universalShareService;
    
    @Override
    public ShareLink createShareLink(Long diaryId, Long userId) {
        // 使用新的通用服务创建分享链接
        UniversalShareLink universalLink = universalShareService.createDiaryShareLink(diaryId, userId);
        
        // 转换为旧的ShareLink格式以保持兼容性
        ShareLink shareLink = new ShareLink();
        shareLink.setId(universalLink.getId());
        shareLink.setDiaryId(universalLink.getTargetId());
        shareLink.setShareToken(universalLink.getShareToken());
        shareLink.setExpiresAt(universalLink.getExpiresAt());
        shareLink.setCreatedAt(universalLink.getCreatedAt());
        shareLink.setIsActive(universalLink.getIsActive());
        
        return shareLink;
    }
    
    @Override
    @Transactional(readOnly = true)
    public SharedDiaryDTO getDiaryByShareToken(String shareToken) {
        return universalShareService.getDiaryByShareToken(shareToken);
    }
    
    @Override
    public boolean isValidShareLink(String shareToken) {
        return universalShareService.isValidDiaryShareLink(shareToken);
    }
    
    @Override
    public void cleanupExpiredLinks() {
        universalShareService.cleanupExpiredDiaryLinks();
    }
    
    @Override
    public LetterShareLink createLetterShareLink(Long letterId, Long userId) {
        // 使用新的通用服务创建分享链接
        UniversalShareLink universalLink = universalShareService.createLetterShareLink(letterId, userId);
        
        // 转换为旧的LetterShareLink格式以保持兼容性
        LetterShareLink shareLink = new LetterShareLink();
        shareLink.setId(universalLink.getId());
        shareLink.setLetterId(universalLink.getTargetId());
        shareLink.setShareToken(universalLink.getShareToken());
        shareLink.setExpiresAt(universalLink.getExpiresAt());
        shareLink.setCreatedAt(universalLink.getCreatedAt());
        shareLink.setIsActive(universalLink.getIsActive());
        
        return shareLink;
    }
    
    @Override
    @Transactional(readOnly = true)
    public SharedLetterDTO getLetterByShareToken(String shareToken) {
        return universalShareService.getLetterByShareToken(shareToken);
    }
    
    @Override
    public boolean isValidLetterShareLink(String shareToken) {
        return universalShareService.isValidLetterShareLink(shareToken);
    }
    
    @Override
    public void cleanupExpiredLetterLinks() {
        universalShareService.cleanupExpiredLetterLinks();
    }
}
