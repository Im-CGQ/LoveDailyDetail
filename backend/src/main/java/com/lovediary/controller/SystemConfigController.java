package com.lovediary.controller;

import com.lovediary.dto.ApiResponse;
import com.lovediary.dto.SystemConfigDTO;
import com.lovediary.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system-config")
@CrossOrigin(origins = "*")
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 获取所有配置
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<SystemConfigDTO>>> getAllConfigs() {
        try {
            List<SystemConfigDTO> configs = systemConfigService.getAllConfigs();
            return ResponseEntity.ok(ApiResponse.success(configs));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取配置失败: " + e.getMessage()));
        }
    }

    /**
     * 获取用户配置（包括全局配置）
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<SystemConfigDTO>>> getUserConfigs(@PathVariable Long userId) {
        try {
            List<SystemConfigDTO> configs = systemConfigService.getUserConfigs(userId);
            return ResponseEntity.ok(ApiResponse.success(configs));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取用户配置失败: " + e.getMessage()));
        }
    }

    /**
     * 获取全局配置
     */
    @GetMapping("/global")
    public ResponseEntity<ApiResponse<List<SystemConfigDTO>>> getGlobalConfigs() {
        try {
            List<SystemConfigDTO> configs = systemConfigService.getGlobalConfigs();
            return ResponseEntity.ok(ApiResponse.success(configs));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取全局配置失败: " + e.getMessage()));
        }
    }

    /**
     * 根据配置键获取配置
     */
    @GetMapping("/key/{configKey}")
    public ResponseEntity<ApiResponse<SystemConfigDTO>> getConfigByKey(@PathVariable String configKey) {
        try {
            SystemConfigDTO config = systemConfigService.getConfigByKey(configKey);
            if (config != null) {
                return ResponseEntity.ok(ApiResponse.success(config));
            } else {
                return ResponseEntity.ok(ApiResponse.error("配置不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取配置失败: " + e.getMessage()));
        }
    }

    /**
     * 保存或更新配置
     */
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<SystemConfigDTO>> saveConfig(@RequestBody SystemConfigDTO configDTO) {
        try {
            SystemConfigDTO savedConfig = systemConfigService.saveConfig(configDTO);
            return ResponseEntity.ok(ApiResponse.success(savedConfig));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("保存配置失败: " + e.getMessage()));
        }
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/delete/{configKey}/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteConfig(@PathVariable String configKey, @PathVariable Long userId) {
        try {
            systemConfigService.deleteConfig(configKey, userId);
            return ResponseEntity.ok(ApiResponse.success("删除成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("删除配置失败: " + e.getMessage()));
        }
    }

    /**
     * 获取在一起的时间
     */
    @GetMapping("/together-date")
    public ResponseEntity<ApiResponse<String>> getTogetherDate() {
        try {
            String togetherDate = systemConfigService.getTogetherDate();
            return ResponseEntity.ok(ApiResponse.success(togetherDate));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取在一起时间失败: " + e.getMessage()));
        }
    }

    /**
     * 设置在一起的时间
     */
    @PostMapping("/together-date")
    public ResponseEntity<ApiResponse<String>> setTogetherDate(@RequestBody Map<String, String> request) {
        try {
            String togetherDate = request.get("togetherDate");
            systemConfigService.setTogetherDate(togetherDate);
            return ResponseEntity.ok(ApiResponse.success("设置成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("设置在一起时间失败: " + e.getMessage()));
        }
    }

    /**
     * 获取背景音乐自动播放配置
     */
    @GetMapping("/background-music-autoplay")
    public ResponseEntity<ApiResponse<Boolean>> getBackgroundMusicAutoplay() {
        try {
            boolean autoplay = systemConfigService.getBackgroundMusicAutoplay();
            return ResponseEntity.ok(ApiResponse.success(autoplay));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取背景音乐自动播放配置失败: " + e.getMessage()));
        }
    }

    /**
     * 设置背景音乐自动播放配置
     */
    @PostMapping("/background-music-autoplay")
    public ResponseEntity<ApiResponse<String>> setBackgroundMusicAutoplay(@RequestBody Map<String, Boolean> request) {
        try {
            Boolean autoplay = request.get("autoplay");
            systemConfigService.setBackgroundMusicAutoplay(autoplay);
            return ResponseEntity.ok(ApiResponse.success("设置成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("设置背景音乐自动播放配置失败: " + e.getMessage()));
        }
    }

    /**
     * 获取分享过期时间配置
     */
    @GetMapping("/share-expire-minutes")
    public ResponseEntity<ApiResponse<Integer>> getShareExpireMinutes() {
        try {
            Integer minutes = systemConfigService.getShareExpireMinutes();
            return ResponseEntity.ok(ApiResponse.success(minutes));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取分享过期时间配置失败: " + e.getMessage()));
        }
    }

    /**
     * 设置分享过期时间配置
     */
    @PostMapping("/share-expire-minutes")
    public ResponseEntity<ApiResponse<String>> setShareExpireMinutes(@RequestBody Map<String, Integer> request) {
        try {
            Integer minutes = request.get("minutes");
            systemConfigService.setShareExpireMinutes(minutes);
            return ResponseEntity.ok(ApiResponse.success("设置成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("设置分享过期时间配置失败: " + e.getMessage()));
        }
    }

    /**
     * 获取配置的Map形式
     */
    @GetMapping("/config-map/{userId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getConfigMap(@PathVariable Long userId) {
        try {
            Map<String, Object> configMap = systemConfigService.getConfigMap(userId);
            return ResponseEntity.ok(ApiResponse.success(configMap));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取配置映射失败: " + e.getMessage()));
        }
    }
}
