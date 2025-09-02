-- 初始化纪念日和下次见面日配置
-- 执行前请确保 system_configs 表已存在

-- 插入纪念日配置（JSON格式，包含示例数据）
INSERT INTO system_configs (config_key, config_value, config_type, description, user_id, created_at, updated_at) 
VALUES ('anniversary_dates', '[]', 'JSON', '纪念日列表', NULL, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    config_value = VALUES(config_value),
    updated_at = NOW();

-- 插入下次见面日期配置
INSERT INTO system_configs (config_key, config_value, config_type, description, user_id, created_at, updated_at) 
VALUES ('next_meeting_date', '', 'STRING', '下次见面日期', NULL, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    config_value = VALUES(config_value),
    updated_at = NOW();

-- 查看插入结果
SELECT * FROM system_configs WHERE config_key IN ('anniversary_dates', 'next_meeting_date');



