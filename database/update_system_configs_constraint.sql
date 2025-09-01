-- 更新system_configs表约束
USE love_diary;

-- 删除旧的唯一性约束
ALTER TABLE system_configs DROP INDEX config_key;

-- 添加新的组合唯一性约束
ALTER TABLE system_configs ADD CONSTRAINT unique_config_key_user UNIQUE (config_key, user_id);

-- 验证约束
SHOW CREATE TABLE system_configs;
