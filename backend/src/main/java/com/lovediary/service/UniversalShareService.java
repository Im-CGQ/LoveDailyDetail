package com.lovediary.service;

import com.lovediary.entity.UniversalShareLink;
import com.lovediary.dto.SharedDiaryDTO;
import com.lovediary.dto.SharedLetterDTO;

public interface UniversalShareService {
    
    /**
     * 创建分享链接
     * @param targetId 目标ID（日记ID、信件ID等）
     * @param shareType 分享类型
     * @param userId 用户ID
     * @return 分享链接信息
     */
    UniversalShareLink createShareLink(Long targetId, UniversalShareLink.ShareType shareType, Long userId);
    
    /**
     * 根据分享token和类型获取目标对象
     * @param shareToken 分享token
     * @param shareType 分享类型
     * @return 目标对象信息
     */
    Object getTargetByShareToken(String shareToken, UniversalShareLink.ShareType shareType);
    
    /**
     * 验证分享链接是否有效
     * @param shareToken 分享token
     * @param shareType 分享类型
     * @return 是否有效
     */
    boolean isValidShareLink(String shareToken, UniversalShareLink.ShareType shareType);
    
    /**
     * 清理过期的分享链接
     * @param shareType 分享类型，null表示清理所有类型
     */
    void cleanupExpiredLinks(UniversalShareLink.ShareType shareType);
    
    // 兼容旧API的方法
    
    /**
     * 创建日记分享链接（兼容旧API）
     */
    UniversalShareLink createDiaryShareLink(Long diaryId, Long userId);
    
    /**
     * 创建信件分享链接（兼容旧API）
     */
    UniversalShareLink createLetterShareLink(Long letterId, Long userId);
    
    /**
     * 根据分享token获取日记（兼容旧API）
     */
    SharedDiaryDTO getDiaryByShareToken(String shareToken);
    
    /**
     * 根据分享token获取信件（兼容旧API）
     */
    SharedLetterDTO getLetterByShareToken(String shareToken);
    
    /**
     * 验证日记分享链接是否有效（兼容旧API）
     */
    boolean isValidDiaryShareLink(String shareToken);
    
    /**
     * 验证信件分享链接是否有效（兼容旧API）
     */
    boolean isValidLetterShareLink(String shareToken);
    
    /**
     * 清理过期的日记分享链接（兼容旧API）
     */
    void cleanupExpiredDiaryLinks();
    
    /**
     * 清理过期的信件分享链接（兼容旧API）
     */
    void cleanupExpiredLetterLinks();
}
