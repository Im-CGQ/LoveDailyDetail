package com.lovediary.service.impl;

import com.lovediary.dto.SystemConfigDTO;
import com.lovediary.entity.SystemConfig;
import com.lovediary.entity.User;
import com.lovediary.repository.SystemConfigRepository;
import com.lovediary.repository.UserRepository;
import com.lovediary.service.SystemConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {

    @Autowired
    private SystemConfigRepository systemConfigRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<SystemConfigDTO> getUserConfigs(Long userId) {
        return systemConfigRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SystemConfigDTO getConfigByKeyAndUser(String configKey, Long userId) {
        Optional<SystemConfig> config = systemConfigRepository.findByConfigKeyAndUserId(configKey, userId);
        return config.map(this::convertToDTO).orElse(null);
    }

    @Override
    public SystemConfigDTO saveConfig(SystemConfigDTO configDTO) {
        SystemConfig entity = convertToEntity(configDTO);
        entity = systemConfigRepository.save(entity);
        return convertToDTO(entity);
    }

    @Override
    public void deleteConfig(String configKey, Long userId) {
        Optional<SystemConfig> config = systemConfigRepository.findByConfigKeyAndUserId(configKey, userId);
        config.ifPresent(systemConfigRepository::delete);
    }

    @Override
    public Map<String, Object> getConfigMap(Long userId) {
        List<SystemConfigDTO> configs = getUserConfigs(userId);
        Map<String, Object> configMap = new HashMap<>();
        
        for (SystemConfigDTO config : configs) {
            String key = config.getConfigKey();
            String value = config.getConfigValue();
            String type = config.getConfigType();
            
            switch (type) {
                case "BOOLEAN":
                    configMap.put(key, Boolean.parseBoolean(value));
                    break;
                case "NUMBER":
                    try {
                        configMap.put(key, Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        configMap.put(key, value);
                    }
                    break;
                case "JSON":
                    configMap.put(key, value);
                    break;
                default:
                    configMap.put(key, value);
                    break;
            }
        }
        
        return configMap;
    }

    // ========== 用户特定配置方法实现 ==========
    
    @Override
    public String getTogetherDateByUserId(Long userId) {
        if (userId == null) {
            return "2024-01-01";
        }
        
        Optional<SystemConfig> config = systemConfigRepository.findByConfigKeyAndUserId("together_date", userId);
        return config.map(SystemConfig::getConfigValue).orElse("2024-01-01");
    }

    @Override
    public void setTogetherDateByUserId(Long userId, String togetherDate) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        // 获取用户信息
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        // 设置当前用户的配置
        setConfigForUser("together_date", togetherDate, "STRING", "在一起的时间", userId);
        
        // 如果用户有伴侣，同步设置伴侣的配置
        if (user.getPartnerId() != null) {
            setConfigForUser("together_date", togetherDate, "STRING", "在一起的时间", user.getPartnerId());
        }
    }

    @Override
    public boolean getBackgroundMusicAutoplayByUserId(Long userId) {
        if (userId == null) {
            return true;
        }
        
        Optional<SystemConfig> config = systemConfigRepository.findByConfigKeyAndUserId("background_music_autoplay", userId);
        return config.map(c -> Boolean.parseBoolean(c.getConfigValue())).orElse(true);
    }

    @Override
    public void setBackgroundMusicAutoplayByUserId(Long userId, boolean autoplay) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        // 设置用户个人配置（不同步给伴侣）
        setConfigForUser("background_music_autoplay", String.valueOf(autoplay), "BOOLEAN", "背景音乐是否自动播放", userId);
    }

    @Override
    public Integer getShareExpireMinutesByUserId(Long userId) {
        if (userId == null) {
            return 60;
        }
        
        Optional<SystemConfig> config = systemConfigRepository.findByConfigKeyAndUserId("share_expire_minutes", userId);
        return config.map(c -> Integer.parseInt(c.getConfigValue())).orElse(60);
    }

    @Override
    public void setShareExpireMinutesByUserId(Long userId, Integer minutes) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        // 设置用户个人配置（不同步给伴侣）
        setConfigForUser("share_expire_minutes", String.valueOf(minutes), "NUMBER", "分享链接过期时间（分钟）", userId);
    }

    @Override
    public String getAnniversaryDatesByUserId(Long userId) {
        if (userId == null) {
            return "[]";
        }
        
        Optional<SystemConfig> config = systemConfigRepository.findByConfigKeyAndUserId("anniversary_dates", userId);
        return config.map(SystemConfig::getConfigValue).orElse("[]");
    }

    @Override
    public void setAnniversaryDatesByUserId(Long userId, String anniversaryDates) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        // 获取用户信息
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        // 设置当前用户的配置
        setConfigForUser("anniversary_dates", anniversaryDates, "JSON", "纪念日列表", userId);
        
        // 如果用户有伴侣，同步设置伴侣的配置
        if (user.getPartnerId() != null) {
            setConfigForUser("anniversary_dates", anniversaryDates, "JSON", "纪念日列表", user.getPartnerId());
        }
    }

    @Override
    public String getNextMeetingDateByUserId(Long userId) {
        if (userId == null) {
            return "";
        }
        
        Optional<SystemConfig> config = systemConfigRepository.findByConfigKeyAndUserId("next_meeting_date", userId);
        return config.map(SystemConfig::getConfigValue).orElse("");
    }

    @Override
    public void setNextMeetingDateByUserId(Long userId, String nextMeetingDate) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        // 获取用户信息
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        // 设置当前用户的配置
        setConfigForUser("next_meeting_date", nextMeetingDate, "STRING", "下次见面日期", userId);
        
        // 如果用户有伴侣，同步设置伴侣的配置
        if (user.getPartnerId() != null) {
            setConfigForUser("next_meeting_date", nextMeetingDate, "STRING", "下次见面日期", user.getPartnerId());
        }
    }

    @Override
    public String getLetterBackgroundMusicByUserId(Long userId) {
        if (userId == null) {
            return "";
        }
        
        Optional<SystemConfig> config = systemConfigRepository.findByConfigKeyAndUserId("letter_background_music", userId);
        return config.map(SystemConfig::getConfigValue).orElse("");
    }

    @Override
    public void setLetterBackgroundMusicByUserId(Long userId, String musicUrl) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        // 设置用户个人配置（不同步给伴侣）
        setConfigForUser("letter_background_music", musicUrl, "STRING", "看信背景音乐", userId);
    }

    // ========== 默认配置方法实现 ==========
    
    @Override
    public Integer getDefaultShareExpireMinutes() {
        return 60; // 默认60分钟
    }
    
    @Override
    public boolean getDefaultBackgroundMusicAutoplay() {
        return true; // 默认自动播放
    }
    
    @Override
    public String getDefaultTogetherDate() {
        return "2024-01-01"; // 默认在一起时间
    }
    
    @Override
    public String getDefaultAnniversaryDates() {
        return "[]"; // 默认空纪念日列表
    }
    
    @Override
    public String getDefaultNextMeetingDate() {
        return ""; // 默认空的下次见面日期
    }

    /**
     * 辅助方法：为用户设置配置
     */
    private void setConfigForUser(String configKey, String configValue, String configType, String description, Long userId) {
        // 先检查配置是否存在
        Optional<SystemConfig> existingConfig = systemConfigRepository.findByConfigKeyAndUserId(configKey, userId);
        if (existingConfig.isPresent()) {
            // 如果存在，更新现有配置
            SystemConfig config = existingConfig.get();
            config.setConfigValue(configValue);
            systemConfigRepository.save(config);
        } else {
            // 如果不存在，创建新配置
            SystemConfig config = new SystemConfig();
            config.setConfigKey(configKey);
            config.setConfigValue(configValue);
            config.setConfigType(configType);
            config.setDescription(description);
            config.setUserId(userId);
            systemConfigRepository.save(config);
        }
    }

    private SystemConfigDTO convertToDTO(SystemConfig entity) {
        SystemConfigDTO dto = new SystemConfigDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private SystemConfig convertToEntity(SystemConfigDTO dto) {
        SystemConfig entity = new SystemConfig();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}