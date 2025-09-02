package com.lovediary.service.impl;

import com.lovediary.dto.SystemConfigDTO;
import com.lovediary.entity.SystemConfig;
import com.lovediary.repository.SystemConfigRepository;
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

    @Override
    public List<SystemConfigDTO> getAllConfigs() {
        return systemConfigRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SystemConfigDTO> getUserConfigs(Long userId) {
        return systemConfigRepository.findByUserIdOrGlobal(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SystemConfigDTO> getGlobalConfigs() {
        return systemConfigRepository.findByUserIdIsNull().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SystemConfigDTO getConfigByKey(String configKey) {
        Optional<SystemConfig> config = systemConfigRepository.findByConfigKey(configKey);
        return config.map(this::convertToDTO).orElse(null);
    }

    @Override
    public SystemConfigDTO getConfigByKeyAndUser(String configKey, Long userId) {
        Optional<SystemConfig> config = systemConfigRepository.findByConfigKeyAndUserId(configKey, userId);
        return config.map(this::convertToDTO).orElse(null);
    }

    @Override
    public SystemConfigDTO saveConfig(SystemConfigDTO configDTO) {
        // 如果是更新操作，需要先获取现有实体的ID
        if (configDTO.getId() == null) {
            // 尝试查找现有配置
            Optional<SystemConfig> existingConfig = systemConfigRepository.findByConfigKey(configDTO.getConfigKey());
            if (existingConfig.isPresent()) {
                // 如果找到现有配置，设置ID以进行更新
                configDTO.setId(existingConfig.get().getId());
            }
        }
        
        SystemConfig config = convertToEntity(configDTO);
        config = systemConfigRepository.save(config);
        return convertToDTO(config);
    }

    @Override
    public void deleteConfig(String configKey, Long userId) {
        Optional<SystemConfig> config = systemConfigRepository.findByConfigKeyAndUserId(configKey, userId);
        config.ifPresent(systemConfigRepository::delete);
    }

    @Override
    public String getTogetherDate() {
        SystemConfigDTO config = getConfigByKey("together_date");
        return config != null ? config.getConfigValue() : "2024-01-01";
    }

    @Override
    public void setTogetherDate(String togetherDate) {
        // 先检查配置是否存在
        SystemConfigDTO existingConfig = getConfigByKey("together_date");
        if (existingConfig != null) {
            // 如果存在，更新现有配置
            existingConfig.setConfigValue(togetherDate);
            saveConfig(existingConfig);
        } else {
            // 如果不存在，创建新配置
            SystemConfigDTO config = new SystemConfigDTO();
            config.setConfigKey("together_date");
            config.setConfigValue(togetherDate);
            config.setConfigType("STRING");
            config.setDescription("在一起的时间");
            config.setUserId(null); // 全局配置
            saveConfig(config);
        }
    }

    @Override
    public boolean getBackgroundMusicAutoplay() {
        SystemConfigDTO config = getConfigByKey("background_music_autoplay");
        return config != null ? Boolean.parseBoolean(config.getConfigValue()) : true;
    }

    @Override
    public void setBackgroundMusicAutoplay(boolean autoplay) {
        // 先检查配置是否存在
        SystemConfigDTO existingConfig = getConfigByKey("background_music_autoplay");
        if (existingConfig != null) {
            // 如果存在，更新现有配置
            existingConfig.setConfigValue(String.valueOf(autoplay));
            saveConfig(existingConfig);
        } else {
            // 如果不存在，创建新配置
            SystemConfigDTO config = new SystemConfigDTO();
            config.setConfigKey("background_music_autoplay");
            config.setConfigValue(String.valueOf(autoplay));
            config.setConfigType("BOOLEAN");
            config.setDescription("背景音乐是否自动播放");
            config.setUserId(null); // 全局配置
            saveConfig(config);
        }
    }

    @Override
    public Integer getShareExpireMinutes() {
        SystemConfigDTO config = getConfigByKey("share_expire_minutes");
        return config != null ? Integer.parseInt(config.getConfigValue()) : 60;
    }

    @Override
    public void setShareExpireMinutes(Integer minutes) {
        // 先检查配置是否存在
        SystemConfigDTO existingConfig = getConfigByKey("share_expire_minutes");
        if (existingConfig != null) {
            // 如果存在，更新现有配置
            existingConfig.setConfigValue(String.valueOf(minutes));
            saveConfig(existingConfig);
        } else {
            // 如果不存在，创建新配置
            SystemConfigDTO config = new SystemConfigDTO();
            config.setConfigKey("share_expire_minutes");
            config.setConfigValue(String.valueOf(minutes));
            config.setConfigType("NUMBER");
            config.setDescription("分享链接过期时间（分钟）");
            config.setUserId(null); // 全局配置
            saveConfig(config);
        }
    }

    @Override
    public Map<String, Object> getConfigMap(Long userId) {
        List<SystemConfigDTO> configs = getUserConfigs(userId);
        Map<String, Object> configMap = new HashMap<>();
        
        for (SystemConfigDTO config : configs) {
            String key = config.getConfigKey();
            String value = config.getConfigValue();
            String type = config.getConfigType();
            
            // 根据配置类型转换值
            switch (type) {
                case "BOOLEAN":
                    configMap.put(key, Boolean.parseBoolean(value));
                    break;
                case "NUMBER":
                    try {
                        if (value.contains(".")) {
                            configMap.put(key, Double.parseDouble(value));
                        } else {
                            configMap.put(key, Long.parseLong(value));
                        }
                    } catch (NumberFormatException e) {
                        configMap.put(key, value);
                    }
                    break;
                default:
                    configMap.put(key, value);
                    break;
            }
        }
        
        return configMap;
    }

    @Override
    public String getLetterBackgroundMusicByUserId(Long userId) {
        if (userId == null) {
            return "";
        }
        
        Optional<SystemConfig> configOpt = systemConfigRepository.findByConfigKeyAndUserId("letter_background_music", userId);
        return configOpt.map(SystemConfig::getConfigValue).orElse("");
    }

    @Override
    public void setLetterBackgroundMusicByUserId(Long userId, String musicUrl) {
        // 先检查配置是否存在
        SystemConfigDTO existingConfig = getConfigByKeyAndUser("letter_background_music", userId);
        if (existingConfig != null) {
            // 如果存在，更新现有配置
            existingConfig.setConfigValue(musicUrl);
            saveConfig(existingConfig);
        } else {
            // 如果不存在，创建新配置
            SystemConfigDTO config = new SystemConfigDTO();
            config.setConfigKey("letter_background_music");
            config.setConfigValue(musicUrl);
            config.setConfigType("STRING");
            config.setDescription("看信背景音乐");
            config.setUserId(userId);
            saveConfig(config);
        }
    }

    @Override
    public String getAnniversaryDates() {
        SystemConfigDTO config = getConfigByKey("anniversary_dates");
        return config != null ? config.getConfigValue() : "[]";
    }

    @Override
    public void setAnniversaryDates(String anniversaryDates) {
        // 先检查配置是否存在
        SystemConfigDTO existingConfig = getConfigByKey("anniversary_dates");
        if (existingConfig != null) {
            // 如果存在，更新现有配置
            existingConfig.setConfigValue(anniversaryDates);
            saveConfig(existingConfig);
        } else {
            // 如果不存在，创建新配置
            SystemConfigDTO config = new SystemConfigDTO();
            config.setConfigKey("anniversary_dates");
            config.setConfigValue(anniversaryDates);
            config.setConfigType("JSON");
            config.setDescription("纪念日列表");
            config.setUserId(null); // 全局配置
            saveConfig(config);
        }
    }

    @Override
    public String getNextMeetingDate() {
        SystemConfigDTO config = getConfigByKey("next_meeting_date");
        return config != null ? config.getConfigValue() : "";
    }

    @Override
    public void setNextMeetingDate(String nextMeetingDate) {
        // 先检查配置是否存在
        SystemConfigDTO existingConfig = getConfigByKey("next_meeting_date");
        if (existingConfig != null) {
            // 如果存在，更新现有配置
            existingConfig.setConfigValue(nextMeetingDate);
            saveConfig(existingConfig);
        } else {
            // 如果不存在，创建新配置
            SystemConfigDTO config = new SystemConfigDTO();
            config.setConfigKey("next_meeting_date");
            config.setConfigValue(nextMeetingDate);
            config.setConfigType("STRING");
            config.setDescription("下次见面日期");
            config.setUserId(null); // 全局配置
            saveConfig(config);
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
