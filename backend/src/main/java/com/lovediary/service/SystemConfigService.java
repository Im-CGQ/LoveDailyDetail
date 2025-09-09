package com.lovediary.service;

import com.lovediary.dto.SystemConfigDTO;
import java.util.List;
import java.util.Map;

public interface SystemConfigService {
    
    // 基础配置管理
    List<SystemConfigDTO> getUserConfigs(Long userId);
    SystemConfigDTO getConfigByKeyAndUser(String configKey, Long userId);
    SystemConfigDTO saveConfig(SystemConfigDTO configDTO);
    void deleteConfig(String configKey, Long userId);
    Map<String, Object> getConfigMap(Long userId);

    // 用户特定配置方法
    /**
     * 根据用户ID获取在一起时间
     */
    String getTogetherDateByUserId(Long userId);
    
    /**
     * 根据用户ID设置在一起时间（同步给伴侣）
     */
    void setTogetherDateByUserId(Long userId, String togetherDate);
    
    /**
     * 根据用户ID获取背景音乐自动播放配置
     */
    boolean getBackgroundMusicAutoplayByUserId(Long userId);
    
    /**
     * 根据用户ID设置背景音乐自动播放配置
     */
    void setBackgroundMusicAutoplayByUserId(Long userId, boolean autoplay);
    
    /**
     * 根据用户ID获取分享过期时间配置
     */
    Integer getShareExpireMinutesByUserId(Long userId);
    
    /**
     * 根据用户ID设置分享过期时间配置
     */
    void setShareExpireMinutesByUserId(Long userId, Integer minutes);
    
    /**
     * 根据用户ID获取纪念日列表
     */
    String getAnniversaryDatesByUserId(Long userId);
    
    /**
     * 根据用户ID设置纪念日列表（同步给伴侣）
     */
    void setAnniversaryDatesByUserId(Long userId, String anniversaryDates);
    
    /**
     * 根据用户ID获取下次见面日期
     */
    String getNextMeetingDateByUserId(Long userId);
    
    /**
     * 根据用户ID设置下次见面日期（同步给伴侣）
     */
    void setNextMeetingDateByUserId(Long userId, String nextMeetingDate);

    /**
     * 根据用户ID获取看信背景音乐
     */
    String getLetterBackgroundMusicByUserId(Long userId);

    /**
     * 根据用户ID设置看信背景音乐
     */
    void setLetterBackgroundMusicByUserId(Long userId, String musicUrl);

    // ========== 默认配置方法（用于没有用户上下文的情况） ==========
    
    /**
     * 获取默认分享过期时间（分钟）
     */
    Integer getDefaultShareExpireMinutes();
    
    /**
     * 获取默认背景音乐自动播放配置
     */
    boolean getDefaultBackgroundMusicAutoplay();
    
    /**
     * 获取默认在一起时间
     */
    String getDefaultTogetherDate();
    
    /**
     * 获取默认纪念日列表
     */
    String getDefaultAnniversaryDates();
    
    /**
     * 获取默认下次见面日期
     */
    String getDefaultNextMeetingDate();
}
