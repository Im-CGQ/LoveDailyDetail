package com.lovediary.service;

import com.lovediary.entity.Diary;
import com.lovediary.entity.ShareLink;
import com.lovediary.entity.Letter;
import com.lovediary.entity.LetterShareLink;
import com.lovediary.dto.SharedDiaryDTO;
import com.lovediary.dto.SharedLetterDTO;

public interface ShareService {
    
    /**
     * 创建分享链接
     * @param diaryId 日记ID
     * @return 分享链接信息
     */
    ShareLink createShareLink(Long diaryId);
    
    /**
     * 根据分享token获取日记
     * @param shareToken 分享token
     * @return 日记信息
     */
    SharedDiaryDTO getDiaryByShareToken(String shareToken);
    
    /**
     * 验证分享链接是否有效
     * @param shareToken 分享token
     * @return 是否有效
     */
    boolean isValidShareLink(String shareToken);
    
    /**
     * 清理过期的分享链接
     */
    void cleanupExpiredLinks();
    
    /**
     * 创建信件分享链接
     * @param letterId 信件ID
     * @return 分享链接信息
     */
    LetterShareLink createLetterShareLink(Long letterId);
    
    /**
     * 根据分享token获取信件
     * @param shareToken 分享token
     * @return 信件信息
     */
    SharedLetterDTO getLetterByShareToken(String shareToken);
    
    /**
     * 验证信件分享链接是否有效
     * @param shareToken 分享token
     * @return 是否有效
     */
    boolean isValidLetterShareLink(String shareToken);
    
    /**
     * 清理过期的信件分享链接
     */
    void cleanupExpiredLetterLinks();
}
