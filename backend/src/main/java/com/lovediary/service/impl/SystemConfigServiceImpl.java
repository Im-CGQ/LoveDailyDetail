package com.lovediary.service.impl;

import com.lovediary.dto.SystemConfigDTO;
import com.lovediary.entity.SystemConfig;
import com.lovediary.repository.SystemConfigRepository;
import com.lovediary.service.SystemConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        SystemConfigDTO config = new SystemConfigDTO();
        config.setConfigKey("together_date");
        config.setConfigValue(togetherDate);
        config.setConfigType("STRING");
        config.setDescription("在一起的时间");
        config.setUserId(null); // 全局配置
        saveConfig(config);
    }

    @Override
    public boolean getBackgroundMusicAutoplay() {
        SystemConfigDTO config = getConfigByKey("background_music_autoplay");
        return config != null ? Boolean.parseBoolean(config.getConfigValue()) : true;
    }

    @Override
    public void setBackgroundMusicAutoplay(boolean autoplay) {
        SystemConfigDTO config = new SystemConfigDTO();
        config.setConfigKey("background_music_autoplay");
        config.setConfigValue(String.valueOf(autoplay));
        config.setConfigType("BOOLEAN");
        config.setDescription("背景音乐是否自动播放");
        config.setUserId(null); // 全局配置
        saveConfig(config);
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
