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
    Map<String, Object> getConfigMap(Long userId);
}
