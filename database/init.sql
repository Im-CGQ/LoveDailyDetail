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
('我们的第一次约会', '2024-01-15', '今天是我们第一次约会，一起去看了电影，吃了火锅，度过了美好的一天。', 1),
('情人节特别回忆', '2024-02-14', '情人节这天，我们一起去了游乐园，坐了摩天轮，在最高点许下了美好的愿望。', 1),
('春天的野餐', '2024-03-20', '春天来了，我们一起去公园野餐，享受阳光和美食，还有彼此的陪伴。', 1);

-- 插入示例图片
INSERT INTO diary_images (diary_id, image_url) VALUES 
(1, 'https://picsum.photos/400/300?random=1'),
(1, 'https://picsum.photos/400/300?random=2'),
(2, 'https://picsum.photos/400/300?random=3'),
(2, 'https://picsum.photos/400/300?random=4'),
(3, 'https://picsum.photos/400/300?random=5');

-- 插入示例视频
INSERT INTO diary_videos (diary_id, video_url) VALUES 
(1, 'https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_1mb.mp4'),
(2, 'https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_2mb.mp4'); 