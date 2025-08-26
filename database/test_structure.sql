-- 测试新的数据库结构
-- 这个脚本用于验证所有新字段和表是否正确创建

-- 检查背景音乐表
DESCRIBE background_music;

-- 检查日记表的新字段
DESCRIBE diaries;

-- 检查日记图片表的新字段
DESCRIBE diary_images;

-- 检查日记视频表的新字段
DESCRIBE diary_videos;

-- 检查日记背景音乐表的新字段
DESCRIBE diary_background_music;

-- 检查外键约束
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    CONSTRAINT_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE REFERENCED_TABLE_SCHEMA = 'love_diary' 
AND TABLE_NAME IN ('diaries', 'diary_images', 'diary_videos');

-- 检查索引
SHOW INDEX FROM background_music;
SHOW INDEX FROM diaries;
SHOW INDEX FROM diary_images;
SHOW INDEX FROM diary_videos;
SHOW INDEX FROM diary_background_music;
