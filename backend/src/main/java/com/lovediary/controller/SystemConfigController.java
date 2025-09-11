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
     * 获取用户配置
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
     * 获取用户配置映射
     */
    @GetMapping("/user/{userId}/map")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserConfigMap(@PathVariable Long userId) {
        try {
            Map<String, Object> configMap = systemConfigService.getConfigMap(userId);
            return ResponseEntity.ok(ApiResponse.success(configMap));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取用户配置映射失败: " + e.getMessage()));
        }
    }

    /**
     * 保存配置
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

    // ========== 用户特定配置API ==========
    
    /**
     * 根据用户ID获取在一起的时间
     */
    @GetMapping("/together-date/{userId}")
    public ResponseEntity<ApiResponse<String>> getTogetherDateByUserId(@PathVariable Long userId) {
        try {
            String togetherDate = systemConfigService.getTogetherDateByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(togetherDate));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取在一起时间失败: " + e.getMessage()));
        }
    }

    /**
     * 根据用户ID设置在一起的时间（同步伴侣配置）
     */
    @PostMapping("/together-date/{userId}")
    public ResponseEntity<ApiResponse<String>> setTogetherDateByUserId(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        try {
            String togetherDate = request.get("togetherDate");
            systemConfigService.setTogetherDateByUserId(userId, togetherDate);
            return ResponseEntity.ok(ApiResponse.success("设置成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("设置在一起时间失败: " + e.getMessage()));
        }
    }

    /**
     * 根据用户ID获取背景音乐自动播放配置
     */
    @GetMapping("/background-music-autoplay/{userId}")
    public ResponseEntity<ApiResponse<Boolean>> getBackgroundMusicAutoplayByUserId(@PathVariable Long userId) {
        try {
            boolean autoplay = systemConfigService.getBackgroundMusicAutoplayByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(autoplay));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取背景音乐自动播放配置失败: " + e.getMessage()));
        }
    }

    /**
     * 根据用户ID设置背景音乐自动播放配置
     */
    @PostMapping("/background-music-autoplay/{userId}")
    public ResponseEntity<ApiResponse<String>> setBackgroundMusicAutoplayByUserId(@PathVariable Long userId, @RequestBody Map<String, Boolean> request) {
        try {
            Boolean autoplay = request.get("autoplay");
            systemConfigService.setBackgroundMusicAutoplayByUserId(userId, autoplay);
            return ResponseEntity.ok(ApiResponse.success("设置成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("设置背景音乐自动播放配置失败: " + e.getMessage()));
        }
    }

    /**
     * 根据用户ID获取分享过期时间配置
     */
    @GetMapping("/share-expire-minutes/{userId}")
    public ResponseEntity<ApiResponse<Integer>> getShareExpireMinutesByUserId(@PathVariable Long userId) {
        try {
            Integer minutes = systemConfigService.getShareExpireMinutesByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(minutes));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取分享过期时间配置失败: " + e.getMessage()));
        }
    }

    /**
     * 根据用户ID设置分享过期时间配置
     */
    @PostMapping("/share-expire-minutes/{userId}")
    public ResponseEntity<ApiResponse<String>> setShareExpireMinutesByUserId(@PathVariable Long userId, @RequestBody Map<String, Integer> request) {
        try {
            Integer minutes = request.get("minutes");
            systemConfigService.setShareExpireMinutesByUserId(userId, minutes);
            return ResponseEntity.ok(ApiResponse.success("设置成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("设置分享过期时间配置失败: " + e.getMessage()));
        }
    }

    /**
     * 根据用户ID获取纪念日列表
     */
    @GetMapping("/anniversary-dates/{userId}")
    public ResponseEntity<ApiResponse<String>> getAnniversaryDatesByUserId(@PathVariable Long userId) {
        try {
            String anniversaryDates = systemConfigService.getAnniversaryDatesByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(anniversaryDates));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取纪念日列表失败: " + e.getMessage()));
        }
    }

    /**
     * 根据用户ID设置纪念日列表（同步伴侣配置）
     */
    @PostMapping("/anniversary-dates/{userId}")
    public ResponseEntity<ApiResponse<String>> setAnniversaryDatesByUserId(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        try {
            String anniversaryDates = request.get("anniversaryDates");
            systemConfigService.setAnniversaryDatesByUserId(userId, anniversaryDates);
            return ResponseEntity.ok(ApiResponse.success("设置成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("设置纪念日列表失败: " + e.getMessage()));
        }
    }

    /**
     * 根据用户ID获取下次见面日期
     */
    @GetMapping("/next-meeting-date/{userId}")
    public ResponseEntity<ApiResponse<String>> getNextMeetingDateByUserId(@PathVariable Long userId) {
        try {
            String nextMeetingDate = systemConfigService.getNextMeetingDateByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(nextMeetingDate));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取下次见面日期失败: " + e.getMessage()));
        }
    }

    /**
     * 根据用户ID设置下次见面日期（同步伴侣配置）
     */
    @PostMapping("/next-meeting-date/{userId}")
    public ResponseEntity<ApiResponse<String>> setNextMeetingDateByUserId(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        try {
            String nextMeetingDate = request.get("nextMeetingDate");
            systemConfigService.setNextMeetingDateByUserId(userId, nextMeetingDate);
            return ResponseEntity.ok(ApiResponse.success("设置成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("设置下次见面日期失败: " + e.getMessage()));
        }
    }

    /**
     * 根据用户ID获取看信背景音乐
     */
    @GetMapping("/letter-background-music/{userId}")
    public ResponseEntity<ApiResponse<String>> getLetterBackgroundMusicByUserId(@PathVariable Long userId) {
        try {
            String musicUrl = systemConfigService.getLetterBackgroundMusicByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(musicUrl));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取看信背景音乐失败: " + e.getMessage()));
        }
    }

    /**
     * 根据用户ID获取看信背景音乐（公开接口，无需登录）
     */
    @GetMapping("/public/letter-background-music/{userId}")
    public ResponseEntity<ApiResponse<String>> getLetterBackgroundMusicByUserIdPublic(@PathVariable Long userId) {
        try {
            String musicUrl = systemConfigService.getLetterBackgroundMusicByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(musicUrl));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取看信背景音乐失败: " + e.getMessage()));
        }
    }

    /**
     * 根据用户ID获取背景音乐自动播放配置（公开接口，无需登录）
     */
    @GetMapping("/public/background-music-autoplay/{userId}")
    public ResponseEntity<ApiResponse<Boolean>> getBackgroundMusicAutoplayByUserIdPublic(@PathVariable Long userId) {
        try {
            boolean autoplay = systemConfigService.getBackgroundMusicAutoplayByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(autoplay));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取背景音乐自动播放配置失败: " + e.getMessage()));
        }
    }

    /**
     * 设置看信背景音乐配置
     */
    @PostMapping("/letter-background-music")
    public ResponseEntity<ApiResponse<String>> setLetterBackgroundMusic(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String musicUrl = (String) request.get("musicUrl");
            
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户ID不能为空"));
            }
            
            systemConfigService.setLetterBackgroundMusicByUserId(userId, musicUrl);
            return ResponseEntity.ok(ApiResponse.success("设置成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("设置看信背景音乐配置失败: " + e.getMessage()));
        }
    }

    // ========== 默认配置API（用于没有用户上下文的情况） ==========
    
    /**
     * 获取默认分享过期时间
     */
    @GetMapping("/default/share-expire-minutes")
    public ResponseEntity<ApiResponse<Integer>> getDefaultShareExpireMinutes() {
        try {
            Integer minutes = systemConfigService.getDefaultShareExpireMinutes();
            return ResponseEntity.ok(ApiResponse.success(minutes));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取默认分享过期时间失败: " + e.getMessage()));
        }
    }

    /**
     * 获取默认背景音乐自动播放配置
     */
    @GetMapping("/default/background-music-autoplay")
    public ResponseEntity<ApiResponse<Boolean>> getDefaultBackgroundMusicAutoplay() {
        try {
            boolean autoplay = systemConfigService.getDefaultBackgroundMusicAutoplay();
            return ResponseEntity.ok(ApiResponse.success(autoplay));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取默认背景音乐自动播放配置失败: " + e.getMessage()));
        }
    }

    /**
     * 获取默认在一起时间
     */
    @GetMapping("/default/together-date")
    public ResponseEntity<ApiResponse<String>> getDefaultTogetherDate() {
        try {
            String togetherDate = systemConfigService.getDefaultTogetherDate();
            return ResponseEntity.ok(ApiResponse.success(togetherDate));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取默认在一起时间失败: " + e.getMessage()));
        }
    }

    /**
     * 获取默认纪念日列表
     */
    @GetMapping("/default/anniversary-dates")
    public ResponseEntity<ApiResponse<String>> getDefaultAnniversaryDates() {
        try {
            String anniversaryDates = systemConfigService.getDefaultAnniversaryDates();
            return ResponseEntity.ok(ApiResponse.success(anniversaryDates));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取默认纪念日列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取默认下次见面日期
     */
    @GetMapping("/default/next-meeting-date")
    public ResponseEntity<ApiResponse<String>> getDefaultNextMeetingDate() {
        try {
            String nextMeetingDate = systemConfigService.getDefaultNextMeetingDate();
            return ResponseEntity.ok(ApiResponse.success(nextMeetingDate));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取默认下次见面日期失败: " + e.getMessage()));
        }
    }
}