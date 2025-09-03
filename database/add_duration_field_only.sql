USE love_diary;

-- 为movies表添加duration字段（精确时长，秒）
-- 如果字段已存在，会报错但不会影响数据库

-- 添加duration字段
ALTER TABLE movies ADD COLUMN duration INT NULL COMMENT '时长（秒）' AFTER duration_minutes;

-- 添加format字段（视频格式）
ALTER TABLE movies ADD COLUMN format VARCHAR(20) NULL COMMENT '视频格式：MP4, AVI, MOV等' AFTER height;

-- 添加bitrate字段（视频比特率）
ALTER TABLE movies ADD COLUMN bitrate INT NULL COMMENT '视频比特率' AFTER format;

-- 添加fps字段（帧率）
ALTER TABLE movies ADD COLUMN fps DECIMAL(5,2) NULL COMMENT '帧率' AFTER bitrate;

-- 添加view_count字段（观看次数）
ALTER TABLE movies ADD COLUMN view_count INT DEFAULT 0 COMMENT '观看次数' AFTER partner_id;

-- 更新现有数据的duration字段（如果有duration_minutes数据）
UPDATE movies SET duration = duration_minutes * 60 WHERE duration_minutes IS NOT NULL AND duration IS NULL;

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_movies_duration ON movies(duration);
CREATE INDEX IF NOT EXISTS idx_movies_format ON movies(format);
CREATE INDEX IF NOT EXISTS idx_movies_view_count ON movies(view_count);

-- 显示movies表结构
DESCRIBE movies;
