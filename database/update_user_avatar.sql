-- 为users表添加avatar_url字段
-- 检查字段是否存在，如果不存在则添加
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = DATABASE() 
     AND TABLE_NAME = 'users' 
     AND COLUMN_NAME = 'avatar_url') = 0,
    'ALTER TABLE users ADD COLUMN avatar_url VARCHAR(500) NULL COMMENT ''用户头像URL''',
    'SELECT ''avatar_url column already exists'' AS message'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 更新现有用户的头像字段为NULL（如果为NULL的话）
UPDATE users SET avatar_url = NULL WHERE avatar_url IS NULL;
