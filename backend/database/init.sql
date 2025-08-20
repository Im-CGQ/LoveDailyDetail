-- 创建数据库
CREATE DATABASE IF NOT EXISTS love_diary DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE love_diary;

-- 删除已存在的表（如果存在）
DROP TABLE IF EXISTS diary_images;
DROP TABLE IF EXISTS diary_videos;
DROP TABLE IF EXISTS diaries;
DROP TABLE IF EXISTS users;

-- 创建用户表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(100),
    role VARCHAR(10) DEFAULT 'ADMIN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建日记表
CREATE TABLE diaries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    description TEXT,
    background_music_url VARCHAR(500),
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 创建日记图片表
CREATE TABLE diary_images (
    diary_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    FOREIGN KEY (diary_id) REFERENCES diaries(id) ON DELETE CASCADE
);

-- 创建日记视频表
CREATE TABLE diary_videos (
    diary_id BIGINT NOT NULL,
    video_url VARCHAR(500) NOT NULL,
    FOREIGN KEY (diary_id) REFERENCES diaries(id) ON DELETE CASCADE
);

-- 插入默认管理员用户 (用户名: admin, 密码: admin)
INSERT INTO users (username, password, display_name, role) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '管理员', 'ADMIN');

-- 插入示例数据
INSERT INTO diaries (title, date, description, user_id) VALUES 
('我们的第一次约会', '2024-01-15', '今天是我们第一次约会，一起去看了电影，吃了火锅，度过了美好的一天。我们聊了很多，发现彼此有很多共同话题，感觉时间过得特别快。', 1),
('春天的野餐', '2024-03-20', '今天天气很好，我们一起去公园野餐。准备了水果、三明治和饮料，在草地上铺了毯子，一边吃东西一边聊天，非常惬意。', 1),
('夏日海滩之旅', '2024-06-15', '今天去了海边，阳光明媚，海风轻拂。我们一起在沙滩上散步，捡贝壳，拍照留念。海水很蓝，天空也很蓝，一切都那么美好。', 1),
('秋日枫叶之旅', '2024-10-20', '秋天到了，枫叶红了。我们一起去山里看枫叶，漫山遍野的红叶美不胜收。拍了很多照片，留下了美好的回忆。', 1);

-- 插入示例图片
INSERT INTO diary_images (diary_id, image_url) VALUES 
(1, 'https://picsum.photos/400/300?random=1'),
(1, 'https://picsum.photos/400/300?random=2'),
(2, 'https://picsum.photos/400/300?random=3'),
(2, 'https://picsum.photos/400/300?random=4'),
(3, 'https://picsum.photos/400/300?random=5'),
(3, 'https://picsum.photos/400/300?random=6'),
(4, 'https://picsum.photos/400/300?random=7'),
(4, 'https://picsum.photos/400/300?random=8');

-- 插入示例视频
INSERT INTO diary_videos (diary_id, video_url) VALUES 
(1, 'https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_1mb.mp4'),
(2, 'https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_2mb.mp4');

-- 创建索引
CREATE INDEX idx_diaries_date ON diaries(date);
CREATE INDEX idx_diary_images_diary_id ON diary_images(diary_id);
CREATE INDEX idx_diary_videos_diary_id ON diary_videos(diary_id); 