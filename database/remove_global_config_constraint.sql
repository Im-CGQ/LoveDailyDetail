-- 去掉系统全局配置约束的数据库更新脚本
USE love_diary;

-- 删除现有的唯一约束
ALTER TABLE system_configs DROP INDEX config_key;
ALTER TABLE system_configs DROP INDEX unique_user_config;

-- 创建新的唯一约束，只针对有用户ID的配置
-- 这样就不会有全局配置的约束冲突
ALTER TABLE system_configs ADD UNIQUE KEY unique_user_config (config_key, user_id);

-- 删除所有全局配置（user_id为NULL的记录）
DELETE FROM system_configs WHERE user_id IS NULL;

-- 显示更新后的表结构
DESCRIBE system_configs;