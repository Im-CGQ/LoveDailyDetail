-- 创建数据库
CREATE DATABASE IF NOT EXISTS love_diary DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE love_diary;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(100),
    role ENUM('ADMIN', 'USER') DEFAULT 'ADMIN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建日记表
CREATE TABLE IF NOT EXISTS diaries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    description TEXT,
    video_url VARCHAR(500),
    background_music_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建日记图片表
CREATE TABLE IF NOT EXISTS diary_images (
    diary_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    FOREIGN KEY (diary_id) REFERENCES diaries(id) ON DELETE CASCADE
);

-- 插入默认管理员用户 (密码: admin123)
INSERT INTO users (username, password, display_name, role) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '管理员', 'ADMIN');

-- 插入示例数据
INSERT INTO diaries (title, date, description) VALUES 
('我们的第一次约会', '2024-01-15', '今天是我们第一次约会，一起去看了电影，吃了火锅，度过了美好的一天。'),
('情人节特别回忆', '2024-02-14', '情人节这天，我们一起去了游乐园，坐了摩天轮，在最高点许下了美好的愿望。'),
('春天的野餐', '2024-03-20', '春天来了，我们一起去公园野餐，享受阳光和美食，还有彼此的陪伴。');

-- 插入示例图片
INSERT INTO diary_images (diary_id, image_url) VALUES 
(1, 'https://picsum.photos/400/300?random=1'),
(2, 'https://picsum.photos/400/300?random=2'),
(3, 'https://picsum.photos/400/300?random=3'); 