USE love_diary;

-- 检查email字段是否存在，如果不存在则添加
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = 'love_diary' 
     AND TABLE_NAME = 'users' 
     AND COLUMN_NAME = 'email') = 0,
    'ALTER TABLE users ADD COLUMN email VARCHAR(100) UNIQUE COMMENT "邮箱"',
    'SELECT "email字段已存在" as message'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加索引以提高查询性能（如果不存在）
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
     WHERE TABLE_SCHEMA = 'love_diary' 
     AND TABLE_NAME = 'users' 
     AND INDEX_NAME = 'idx_users_email') = 0,
    'CREATE INDEX idx_users_email ON users(email)',
    'SELECT "idx_users_email索引已存在" as message'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
