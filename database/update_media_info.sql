-- 更新日记图片表，添加文件名、宽高信息
ALTER TABLE diary_images 
ADD COLUMN file_name VARCHAR(255) NOT NULL DEFAULT '',
ADD COLUMN width INT NULL,
ADD COLUMN height INT NULL;

-- 更新日记视频表，添加文件名、宽高信息
ALTER TABLE diary_videos 
ADD COLUMN file_name VARCHAR(255) NOT NULL DEFAULT '',
ADD COLUMN width INT NULL,
ADD COLUMN height INT NULL;

-- 为diary_images表添加主键
ALTER TABLE diary_images 
ADD COLUMN id BIGINT AUTO_INCREMENT PRIMARY KEY FIRST;

-- 为diary_videos表添加主键
ALTER TABLE diary_videos 
ADD COLUMN id BIGINT AUTO_INCREMENT PRIMARY KEY FIRST;

-- 创建背景音乐表
CREATE TABLE background_music (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    music_url VARCHAR(500) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    title VARCHAR(255) NULL COMMENT '音乐标题',
    artist VARCHAR(255) NULL COMMENT '艺术家',
    duration INT NULL COMMENT '时长（秒）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建日记背景音乐表
CREATE TABLE diary_background_music (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    diary_id BIGINT NOT NULL,
    music_url VARCHAR(500) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    title VARCHAR(255) NULL COMMENT '音乐标题',
    artist VARCHAR(255) NULL COMMENT '艺术家',
    duration INT NULL COMMENT '时长（秒）',
    FOREIGN KEY (diary_id) REFERENCES diaries(id) ON DELETE CASCADE
);

-- 更新现有数据，将URL中的文件名提取到file_name字段
UPDATE diary_images 
SET file_name = SUBSTRING_INDEX(image_url, '/', -1)
WHERE file_name = '';

UPDATE diary_videos 
SET file_name = SUBSTRING_INDEX(video_url, '/', -1)
WHERE file_name = '';

-- 将现有的背景音乐数据迁移到新的关联表
INSERT INTO diary_background_music (diary_id, music_url, file_name, title)
SELECT 
    id as diary_id,
    background_music_url as music_url,
    SUBSTRING_INDEX(background_music_url, '/', -1) as file_name,
    SUBSTRING_INDEX(SUBSTRING_INDEX(background_music_url, '/', -1), '.', 1) as title
FROM diaries 
WHERE background_music_url IS NOT NULL;

-- 删除日记表中的背景音乐URL字段（可选，如果需要保持兼容性可以保留）
-- ALTER TABLE diaries DROP COLUMN background_music_url;
