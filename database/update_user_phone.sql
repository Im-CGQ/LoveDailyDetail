-- 为users表添加phone字段
USE love_diary;

-- 检查phone字段是否存在，如果不存在则添加
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = 'love_diary' 
     AND TABLE_NAME = 'users' 
     AND COLUMN_NAME = 'phone') = 0,
    'ALTER TABLE users ADD COLUMN phone VARCHAR(11) UNIQUE COMMENT "手机号"',
    'SELECT "phone字段已存在" as message'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 为现有数据设置默认值（如果需要）
-- UPDATE users SET phone = CONCAT('1380000', LPAD(id, 4, '0')) WHERE phone IS NULL;

-- 添加索引以提高查询性能（如果不存在）
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
     WHERE TABLE_SCHEMA = 'love_diary' 
     AND TABLE_NAME = 'users' 
     AND INDEX_NAME = 'idx_users_phone') = 0,
    'CREATE INDEX idx_users_phone ON users(phone)',
    'SELECT "idx_users_phone索引已存在" as message'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
