-- 简单的系统配置约束迁移脚本
USE love_diary;

-- 查看当前表的索引情况
SHOW INDEX FROM system_configs;

-- 删除可能存在的索引（忽略错误）
-- 注意：这些命令可能会报错，但可以忽略

-- 尝试删除 config_key 索引（如果存在）
SET @sql = 'ALTER TABLE system_configs DROP INDEX config_key';
SET @sql = IF(
    (SELECT COUNT(*) FROM information_schema.statistics 
     WHERE table_schema = 'love_diary' 
     AND table_name = 'system_configs' 
     AND index_name = 'config_key') > 0,
    @sql,
    'SELECT "config_key index does not exist" as message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 尝试删除 unique_user_config 索引（如果存在）
SET @sql = 'ALTER TABLE system_configs DROP INDEX unique_user_config';
SET @sql = IF(
    (SELECT COUNT(*) FROM information_schema.statistics 
     WHERE table_schema = 'love_diary' 
     AND table_name = 'system_configs' 
     AND index_name = 'unique_user_config') > 0,
    @sql,
    'SELECT "unique_user_config index does not exist" as message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 创建新的唯一约束
ALTER TABLE system_configs ADD UNIQUE KEY unique_user_config (config_key, user_id);

-- 删除所有全局配置（user_id为NULL的记录）
DELETE FROM system_configs WHERE user_id IS NULL;

-- 显示更新后的表结构
DESCRIBE system_configs;

-- 显示更新后的索引
SHOW INDEX FROM system_configs;
