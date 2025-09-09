-- 最终版本：系统配置约束迁移脚本
USE love_diary;

-- 查看当前表的索引情况
SHOW INDEX FROM system_configs;

-- 删除所有全局配置（user_id为NULL的记录）
DELETE FROM system_configs WHERE user_id IS NULL;

-- 尝试删除可能存在的索引（如果不存在会报错，但可以忽略）
-- 删除 config_key 索引（如果存在）
ALTER TABLE system_configs DROP INDEX IF EXISTS config_key;

-- 删除 unique_user_config 索引（如果存在）
ALTER TABLE system_configs DROP INDEX IF EXISTS unique_user_config;

-- 创建新的唯一约束
ALTER TABLE system_configs ADD UNIQUE KEY unique_user_config (config_key, user_id);

-- 显示更新后的表结构
DESCRIBE system_configs;

-- 显示更新后的索引
SHOW INDEX FROM system_configs;
