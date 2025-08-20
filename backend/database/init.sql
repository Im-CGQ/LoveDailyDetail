-- 创建数据库
CREATE DATABASE IF NOT EXISTS love_diary DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE love_diary;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(100),
    role ENUM('ADMIN', 'USER') DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建日记表
CREATE TABLE IF NOT EXISTS diaries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    date DATE NOT NULL UNIQUE,
    description TEXT,
    video_url VARCHAR(500),
    background_music_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建日记图片表
CREATE TABLE IF NOT EXISTS diary_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    diary_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    FOREIGN KEY (diary_id) REFERENCES diaries(id) ON DELETE CASCADE
);

-- 插入默认管理员用户（密码：admin，使用BCrypt加密）
INSERT INTO users (username, password, display_name, role) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '管理员', 'ADMIN')
ON DUPLICATE KEY UPDATE username=username;

-- 插入示例日记数据
INSERT INTO diaries (title, date, description) VALUES 
('我们的第一次约会', '2024-01-15', '今天是我们第一次约会，一起去看了电影，吃了火锅，度过了美好的一天。我们聊了很多，发现彼此有很多共同话题，感觉时间过得特别快。'),
('春天的野餐', '2024-03-20', '今天天气很好，我们一起去公园野餐。准备了水果、三明治和饮料，在草地上铺了毯子，一边吃东西一边聊天，非常惬意。'),
('夏日海滩之旅', '2024-06-15', '今天去了海边，阳光明媚，海风轻拂。我们一起在沙滩上散步，捡贝壳，拍照留念。海水很蓝，天空也很蓝，一切都那么美好。'),
('秋日枫叶之旅', '2024-10-20', '秋天到了，枫叶红了。我们一起去山里看枫叶，漫山遍野的红叶美不胜收。拍了很多照片，留下了美好的回忆。')
ON DUPLICATE KEY UPDATE title=title;

-- 插入示例图片数据
INSERT INTO diary_images (diary_id, image_url) VALUES 
(1, 'https://picsum.photos/400/300?random=1'),
(1, 'https://picsum.photos/400/300?random=2'),
(2, 'https://picsum.photos/400/300?random=3'),
(2, 'https://picsum.photos/400/300?random=4'),
(3, 'https://picsum.photos/400/300?random=5'),
(3, 'https://picsum.photos/400/300?random=6'),
(4, 'https://picsum.photos/400/300?random=7'),
(4, 'https://picsum.photos/400/300?random=8');

-- 创建索引
CREATE INDEX idx_diaries_date ON diaries(date);
CREATE INDEX idx_diary_images_diary_id ON diary_images(diary_id); 