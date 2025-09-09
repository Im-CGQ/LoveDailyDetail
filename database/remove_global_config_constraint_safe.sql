-- 安全的系统配置约束迁移脚本
USE love_diary;

-- 首先查看当前表的索引情况
SHOW INDEX FROM system_configs;

-- 删除可能存在的索引（如果存在的话）
-- 使用 IF EXISTS 语法（MySQL 8.0+）或者先检查再删除

-- 方法1：使用存储过程来安全删除索引
DELIMITER $$

CREATE PROCEDURE DropIndexIfExists()
BEGIN
    DECLARE index_exists INT DEFAULT 0;
    
    -- 检查 config_key 索引是否存在
    SELECT COUNT(*) INTO index_exists
    FROM information_schema.statistics 
    WHERE table_schema = 'love_diary' 
    AND table_name = 'system_configs' 
    AND index_name = 'config_key';
    
    IF index_exists > 0 THEN
        ALTER TABLE system_configs DROP INDEX config_key;
        SELECT 'Dropped config_key index' as message;
    ELSE
        SELECT 'config_key index does not exist' as message;
    END IF;
    
    -- 检查 unique_user_config 索引是否存在
    SELECT COUNT(*) INTO index_exists
    FROM information_schema.statistics 
    WHERE table_schema = 'love_diary' 
    AND table_name = 'system_configs' 
    AND index_name = 'unique_user_config';
    
    IF index_exists > 0 THEN
        ALTER TABLE system_configs DROP INDEX unique_user_config;
        SELECT 'Dropped unique_user_config index' as message;
    ELSE
        SELECT 'unique_user_config index does not exist' as message;
    END IF;
END$$

DELIMITER ;

-- 执行存储过程
CALL DropIndexIfExists();

-- 删除存储过程
DROP PROCEDURE DropIndexIfExists;

-- 创建新的唯一约束
ALTER TABLE system_configs ADD UNIQUE KEY unique_user_config (config_key, user_id);

-- 删除所有全局配置（user_id为NULL的记录）
DELETE FROM system_configs WHERE user_id IS NULL;

-- 显示更新后的表结构
DESCRIBE system_configs;

-- 显示更新后的索引
SHOW INDEX FROM system_configs;
