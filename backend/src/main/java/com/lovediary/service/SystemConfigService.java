package com.lovediary.service;

import com.lovediary.dto.SystemConfigDTO;
import java.util.List;
import java.util.Map;

public interface SystemConfigService {
    List<SystemConfigDTO> getAllConfigs();
    List<SystemConfigDTO> getUserConfigs(Long userId);
    List<SystemConfigDTO> getGlobalConfigs();
    SystemConfigDTO getConfigByKey(String configKey);
    SystemConfigDTO getConfigByKeyAndUser(String configKey, Long userId);
    SystemConfigDTO saveConfig(SystemConfigDTO configDTO);
    void deleteConfig(String configKey, Long userId);
    String getTogetherDate();
    void setTogetherDate(String togetherDate);
    boolean getBackgroundMusicAutoplay();
    void setBackgroundMusicAutoplay(boolean autoplay);
    Integer getShareExpireMinutes();
    void setShareExpireMinutes(Integer minutes);
    Map<String, Object> getConfigMap(Long userId);

    /**
     * 根据用户ID获取看信背景音乐
     */
    String getLetterBackgroundMusicByUserId(Long userId);

    /**
     * 根据用户ID设置看信背景音乐
     */
    void setLetterBackgroundMusicByUserId(Long userId, String musicUrl);

    /**
     * 获取纪念日列表
     */
    String getAnniversaryDates();

    /**
     * 设置纪念日列表
     */
    void setAnniversaryDates(String anniversaryDates);

    /**
     * 获取下次见面日期
     */
    String getNextMeetingDate();

    /**
     * 设置下次见面日期
     */
    void setNextMeetingDate(String nextMeetingDate);
}
