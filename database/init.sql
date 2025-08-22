-- 创建数据库
CREATE DATABASE IF NOT EXISTS love_diary DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE love_diary;

-- 删除已存在的表（如果存在）
DROP TABLE IF EXISTS chat_records;
DROP TABLE IF EXISTS diary_images;
DROP TABLE IF EXISTS diary_videos;
DROP TABLE IF EXISTS diaries;
DROP TABLE IF EXISTS letters;
DROP TABLE IF EXISTS partner_invitations;
DROP TABLE IF EXISTS system_configs;
DROP TABLE IF EXISTS users;

-- 创建用户表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(100),
    role VARCHAR(10) DEFAULT 'ADMIN',
    partner_id BIGINT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (partner_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 创建系统配置表
CREATE TABLE system_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_type VARCHAR(20) DEFAULT 'STRING' COMMENT '配置类型：STRING, NUMBER, BOOLEAN, JSON',
    description VARCHAR(255) COMMENT '配置描述',
    user_id BIGINT NULL COMMENT '用户ID，NULL表示全局配置',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 创建伴侣邀请表
CREATE TABLE partner_invitations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_user_id BIGINT NOT NULL,
    to_user_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, ACCEPTED, REJECTED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (from_user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (to_user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_invitation (from_user_id, to_user_id)
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

-- 创建信件表
CREATE TABLE letters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    unlock_time TIMESTAMP NOT NULL,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 创建聊天记录表
CREATE TABLE chat_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chat_type VARCHAR(50) NOT NULL COMMENT '聊天类型：微信语音、微信聊天、小红书聊天、自定义类型',
    duration_minutes INT NOT NULL COMMENT '聊天时长（分钟）',
    date DATE NOT NULL COMMENT '聊天日期',
    description TEXT COMMENT '聊天描述或备注',
    custom_type VARCHAR(100) COMMENT '自定义聊天类型',
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 插入默认管理员用户 (用户名: admin, 密码: admin)
INSERT INTO users (username, password, display_name, role) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '管理员', 'ADMIN');

-- 插入默认系统配置
INSERT INTO system_configs (config_key, config_value, config_type, description, user_id) VALUES 
('together_date', '2025-05-30', 'STRING', '在一起的时间', NULL),
('background_music_autoplay', 'true', 'BOOLEAN', '背景音乐是否自动播放', NULL);

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

-- 插入示例信件数据
INSERT INTO letters (title, content, unlock_time, sender_id, receiver_id, is_read) VALUES 
('给未来的自己', '亲爱的自己，希望未来的你能看到这封信时，已经实现了所有的梦想。记住要永远保持初心，勇敢前行！', '2025-01-01 00:00:00', 1, 1, false),
('给伴侣的信', '亲爱的，感谢你一直以来的陪伴和支持。我们的爱情故事还在继续，期待与你一起创造更多美好的回忆。', '2024-12-25 00:00:00', 1, 1, false);

-- 插入示例聊天记录数据
INSERT INTO chat_records (chat_type, duration_minutes, date, description, custom_type, user_id) VALUES 
('微信语音', 45, '2024-01-15', '今天和女朋友聊了很久，聊了很多有趣的话题', NULL, 1),
('微信聊天', 30, '2024-01-14', '日常聊天，分享今天发生的事情', NULL, 1),
('小红书聊天', 20, '2024-01-13', '分享美食和旅行攻略', NULL, 1),
('自定义', 60, '2024-01-12', '视频通话聊天', '视频通话', 1),
('微信语音', 25, '2024-01-11', '睡前语音聊天', NULL, 1);

-- 创建索引
CREATE INDEX idx_diaries_date ON diaries(date);
CREATE INDEX idx_diary_images_diary_id ON diary_images(diary_id);
CREATE INDEX idx_diary_videos_diary_id ON diary_videos(diary_id);
CREATE INDEX idx_partner_invitations_from_user ON partner_invitations(from_user_id);
CREATE INDEX idx_partner_invitations_to_user ON partner_invitations(to_user_id);
CREATE INDEX idx_partner_invitations_status ON partner_invitations(status);
CREATE INDEX idx_letters_sender_id ON letters(sender_id);
CREATE INDEX idx_letters_receiver_id ON letters(receiver_id);
CREATE INDEX idx_letters_unlock_time ON letters(unlock_time);
CREATE INDEX idx_letters_is_read ON letters(is_read);
CREATE INDEX idx_chat_records_user_id ON chat_records(user_id);
CREATE INDEX idx_chat_records_date ON chat_records(date);
CREATE INDEX idx_chat_records_chat_type ON chat_records(chat_type);
CREATE INDEX idx_system_configs_key ON system_configs(config_key);
CREATE INDEX idx_system_configs_user_id ON system_configs(user_id); 