USE love_diary;

-- 为movies表添加duration字段（精确时长，秒）
-- 执行时间: 2024年

-- 添加duration字段
ALTER TABLE movies ADD COLUMN duration INT NULL COMMENT '时长（秒）' AFTER duration_minutes;

-- 更新现有数据的duration字段（如果有duration_minutes数据）
-- 注意：这里只是示例，实际执行时需要根据现有数据情况调整
-- UPDATE movies SET duration = duration_minutes * 60 WHERE duration_minutes IS NOT NULL AND duration IS NULL;

-- 添加索引
CREATE INDEX idx_movies_duration ON movies(duration);

-- 显示表结构
DESCRIBE movies;
