-- 为users表添加avatar_url字段（简化版本）
-- 直接添加字段，如果已存在会报错，但不会影响数据库

ALTER TABLE users ADD COLUMN avatar_url VARCHAR(500) NULL COMMENT '用户头像URL';

-- 更新现有用户的头像字段为NULL
UPDATE users SET avatar_url = NULL WHERE avatar_url IS NULL;
