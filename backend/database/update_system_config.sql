-- 系统配置表更新脚本
USE love_diary;

-- 创建系统配置表
CREATE TABLE IF NOT EXISTS system_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_type VARCHAR(20) DEFAULT 'STRING' COMMENT '配置类型：STRING, NUMBER, BOOLEAN, JSON',
    description VARCHAR(255) COMMENT '配置描述',
    user_id BIGINT NULL COMMENT '用户ID，NULL表示全局配置',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 插入默认系统配置
INSERT INTO system_configs (config_key, config_value, config_type, description, user_id) VALUES 
('together_date', '2024-01-01', 'STRING', '在一起的时间', NULL),
('background_music_autoplay', 'true', 'BOOLEAN', '背景音乐是否自动播放', NULL)
ON DUPLICATE KEY UPDATE 
    config_value = VALUES(config_value),
    updated_at = CURRENT_TIMESTAMP;

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_system_configs_key ON system_configs(config_key);
CREATE INDEX IF NOT EXISTS idx_system_configs_user_id ON system_configs(user_id);
