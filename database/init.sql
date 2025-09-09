-- 创建数据库
CREATE DATABASE IF NOT EXISTS love_diary DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE love_diary;

-- 删除已存在的表（如果存在）
DROP TABLE IF EXISTS movie_room_members;
DROP TABLE IF EXISTS movie_rooms;
DROP TABLE IF EXISTS movies;
DROP TABLE IF EXISTS letter_share_links;
DROP TABLE IF EXISTS diary_share_links;
DROP TABLE IF EXISTS chat_records;
DROP TABLE IF EXISTS diary_background_music;
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
    avatar_url VARCHAR(500) NULL COMMENT '用户头像URL',
    email VARCHAR(100) NULL COMMENT '用户邮箱',
    phone VARCHAR(20) NULL COMMENT '用户手机号',
    role VARCHAR(10) DEFAULT 'USER' COMMENT '用户角色：USER, ADMIN',
    partner_id BIGINT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (partner_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 创建系统配置表
CREATE TABLE system_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_type VARCHAR(20) DEFAULT 'STRING' COMMENT '配置类型：STRING, NUMBER, BOOLEAN, JSON',
    description VARCHAR(255) COMMENT '配置描述',
    user_id BIGINT NOT NULL COMMENT '用户ID，不再支持全局配置',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_config (config_key, user_id)
);

-- 创建伴侣邀请表
CREATE TABLE partner_invitations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_user_id BIGINT NOT NULL,
    to_user_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '邀请状态：PENDING, ACCEPTED, REJECTED',
    message TEXT COMMENT '邀请消息',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (from_user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (to_user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_invitation (from_user_id, to_user_id)
);

-- 创建日记表
CREATE TABLE diaries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL COMMENT '日记标题',
    date DATE NOT NULL COMMENT '日记日期',
    description TEXT COMMENT '日记内容',
    mood VARCHAR(50) NULL COMMENT '心情状态',
    weather VARCHAR(50) NULL COMMENT '天气情况',
    user_id BIGINT NOT NULL COMMENT '创建者用户ID',
    partner_id BIGINT NULL COMMENT '伴侣用户ID',
    is_public BOOLEAN DEFAULT FALSE COMMENT '是否公开',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (partner_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 创建日记图片表
CREATE TABLE diary_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    diary_id BIGINT NOT NULL COMMENT '日记ID',
    image_url VARCHAR(500) NOT NULL COMMENT '图片URL',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    file_size BIGINT NULL COMMENT '文件大小（字节）',
    width INT NULL COMMENT '图片宽度',
    height INT NULL COMMENT '图片高度',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (diary_id) REFERENCES diaries(id) ON DELETE CASCADE
);

-- 创建日记视频表
CREATE TABLE diary_videos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    diary_id BIGINT NOT NULL COMMENT '日记ID',
    video_url VARCHAR(500) NOT NULL COMMENT '视频URL',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    file_size BIGINT NULL COMMENT '文件大小（字节）',
    width INT NULL COMMENT '视频宽度',
    height INT NULL COMMENT '视频高度',
    duration INT NULL COMMENT '视频时长（秒）',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (diary_id) REFERENCES diaries(id) ON DELETE CASCADE
);

-- 创建日记背景音乐表
CREATE TABLE diary_background_music (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    diary_id BIGINT NOT NULL COMMENT '日记ID',
    music_url VARCHAR(500) NOT NULL COMMENT '音乐URL',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    title VARCHAR(255) NULL COMMENT '音乐标题',
    artist VARCHAR(255) NULL COMMENT '艺术家',
    duration INT NULL COMMENT '时长（秒）',
    file_size BIGINT NULL COMMENT '文件大小（字节）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (diary_id) REFERENCES diaries(id) ON DELETE CASCADE
);

-- 创建信件表
CREATE TABLE letters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL COMMENT '信件标题',
    content TEXT NOT NULL COMMENT '信件内容',
    unlock_time TIMESTAMP NOT NULL COMMENT '解锁时间',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    receiver_id BIGINT NOT NULL COMMENT '接收者ID',
    is_read BOOLEAN DEFAULT FALSE COMMENT '是否已读',
    is_favorite BOOLEAN DEFAULT FALSE COMMENT '是否收藏',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 创建电影表
CREATE TABLE movies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL COMMENT '电影标题',
    description TEXT COMMENT '电影描述',
    cover_url VARCHAR(500) COMMENT '封面图片URL',
    movie_url VARCHAR(500) COMMENT '电影文件URL',
    file_name VARCHAR(255) COMMENT '文件名',
    is_public BOOLEAN DEFAULT FALSE COMMENT '是否公开',
    duration_minutes INT COMMENT '电影时长（分钟）- 向后兼容',
    duration INT COMMENT '电影时长（秒）- 精确时长',
    file_size BIGINT COMMENT '文件大小（字节）',
    width INT COMMENT '视频宽度',
    height INT COMMENT '视频高度',
    format VARCHAR(20) NULL COMMENT '视频格式：MP4, AVI, MOV等',
    bitrate INT NULL COMMENT '视频比特率',
    fps DECIMAL(5,2) NULL COMMENT '帧率',
    user_id BIGINT NOT NULL COMMENT '创建者用户ID',
    partner_id BIGINT NULL COMMENT '伴侣用户ID',
    view_count INT DEFAULT 0 COMMENT '观看次数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (partner_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 创建电影房间表
CREATE TABLE movie_rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_name VARCHAR(255) NOT NULL COMMENT '房间名称',
    room_code VARCHAR(10) NOT NULL UNIQUE COMMENT '房间代码',
    play_time DOUBLE DEFAULT 0.0 COMMENT '当前播放时间（秒）',
    is_playing BOOLEAN DEFAULT FALSE COMMENT '是否正在播放',
    last_updated_by BIGINT NULL COMMENT '最后操作的用户ID',
    last_updated_at TIMESTAMP NULL COMMENT '最后更新时间',
    movie_id BIGINT NOT NULL COMMENT '电影ID',
    creator_id BIGINT NOT NULL COMMENT '创建者用户ID',
    max_members INT DEFAULT 10 COMMENT '最大成员数',
    is_private BOOLEAN DEFAULT FALSE COMMENT '是否私密房间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (last_updated_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 创建电影房间成员表
CREATE TABLE movie_room_members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL COMMENT '房间ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    is_online BOOLEAN DEFAULT TRUE COMMENT '是否在线',
    last_seen TIMESTAMP NULL COMMENT '最后在线时间',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    role VARCHAR(20) DEFAULT 'MEMBER' COMMENT '成员角色：CREATOR, MODERATOR, MEMBER',
    FOREIGN KEY (room_id) REFERENCES movie_rooms(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_room_user (room_id, user_id)
);

-- 创建聊天记录表
CREATE TABLE chat_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chat_type VARCHAR(50) NOT NULL COMMENT '聊天类型：微信语音、微信聊天、小红书聊天、自定义类型',
    duration_minutes INT NOT NULL COMMENT '聊天时长（分钟）',
    date DATE NOT NULL COMMENT '聊天日期',
    description TEXT COMMENT '聊天描述或备注',
    custom_type VARCHAR(100) COMMENT '自定义聊天类型',
    mood VARCHAR(50) NULL COMMENT '聊天心情',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    partner_id BIGINT NULL COMMENT '伴侣用户ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (partner_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 创建日记分享链接表
CREATE TABLE diary_share_links (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    diary_id BIGINT NOT NULL COMMENT '日记ID',
    share_token VARCHAR(32) NOT NULL UNIQUE COMMENT '分享令牌',
    expires_at TIMESTAMP NOT NULL COMMENT '过期时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    view_count INT DEFAULT 0 COMMENT '查看次数',
    FOREIGN KEY (diary_id) REFERENCES diaries(id) ON DELETE CASCADE
);

-- 创建信件分享链接表
CREATE TABLE letter_share_links (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    letter_id BIGINT NOT NULL COMMENT '信件ID',
    share_token VARCHAR(32) NOT NULL UNIQUE COMMENT '分享令牌',
    expires_at TIMESTAMP NOT NULL COMMENT '过期时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    view_count INT DEFAULT 0 COMMENT '查看次数',
    FOREIGN KEY (letter_id) REFERENCES letters(id) ON DELETE CASCADE
);

-- 插入默认管理员用户 (用户名: admin, 密码: admin)
INSERT INTO users (username, password, display_name, role) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '管理员', 'ADMIN');

-- 不再插入默认系统配置，所有配置都需要用户ID

-- 创建索引
-- 用户相关索引
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX idx_users_partner_id ON users(partner_id);
CREATE INDEX idx_users_role ON users(role);

-- 日记相关索引
CREATE INDEX idx_diaries_date ON diaries(date);
CREATE INDEX idx_diaries_user_id ON diaries(user_id);
CREATE INDEX idx_diaries_partner_id ON diaries(partner_id);
CREATE INDEX idx_diaries_user_partner ON diaries(user_id, partner_id);
CREATE INDEX idx_diaries_is_public ON diaries(is_public);
CREATE INDEX idx_diaries_mood ON diaries(mood);
CREATE INDEX idx_diary_images_diary_id ON diary_images(diary_id);
CREATE INDEX idx_diary_images_sort_order ON diary_images(sort_order);
CREATE INDEX idx_diary_videos_diary_id ON diary_videos(diary_id);
CREATE INDEX idx_diary_videos_sort_order ON diary_videos(sort_order);
CREATE INDEX idx_diary_background_music_diary_id ON diary_background_music(diary_id);

-- 伴侣邀请相关索引
CREATE INDEX idx_partner_invitations_from_user ON partner_invitations(from_user_id);
CREATE INDEX idx_partner_invitations_to_user ON partner_invitations(to_user_id);
CREATE INDEX idx_partner_invitations_status ON partner_invitations(status);

-- 信件相关索引
CREATE INDEX idx_letters_sender_id ON letters(sender_id);
CREATE INDEX idx_letters_receiver_id ON letters(receiver_id);
CREATE INDEX idx_letters_unlock_time ON letters(unlock_time);
CREATE INDEX idx_letters_is_read ON letters(is_read);
CREATE INDEX idx_letters_is_favorite ON letters(is_favorite);

-- 聊天记录相关索引
CREATE INDEX idx_chat_records_user_id ON chat_records(user_id);
CREATE INDEX idx_chat_records_partner_id ON chat_records(partner_id);
CREATE INDEX idx_chat_records_user_partner ON chat_records(user_id, partner_id);
CREATE INDEX idx_chat_records_date ON chat_records(date);
CREATE INDEX idx_chat_records_chat_type ON chat_records(chat_type);
CREATE INDEX idx_chat_records_mood ON chat_records(mood);

-- 系统配置相关索引
CREATE INDEX idx_system_configs_key ON system_configs(config_key);
CREATE INDEX idx_system_configs_user_id ON system_configs(user_id);

-- 分享链接相关索引
CREATE INDEX idx_diary_share_links_token ON diary_share_links(share_token);
CREATE INDEX idx_diary_share_links_diary_id ON diary_share_links(diary_id);
CREATE INDEX idx_diary_share_links_expires_at ON diary_share_links(expires_at);
CREATE INDEX idx_diary_share_links_is_active ON diary_share_links(is_active);
CREATE INDEX idx_letter_share_links_token ON letter_share_links(share_token);
CREATE INDEX idx_letter_share_links_letter_id ON letter_share_links(letter_id);
CREATE INDEX idx_letter_share_links_expires_at ON letter_share_links(expires_at);
CREATE INDEX idx_letter_share_links_is_active ON letter_share_links(is_active);

-- 电影相关索引
CREATE INDEX idx_movies_user_id ON movies(user_id);
CREATE INDEX idx_movies_partner_id ON movies(partner_id);
CREATE INDEX idx_movies_is_public ON movies(is_public);
CREATE INDEX idx_movies_created_at ON movies(created_at);
CREATE INDEX idx_movies_duration ON movies(duration);
CREATE INDEX idx_movies_duration_minutes ON movies(duration_minutes);
CREATE INDEX idx_movies_format ON movies(format);
CREATE INDEX idx_movies_view_count ON movies(view_count);

-- 电影房间相关索引
CREATE INDEX idx_movie_rooms_room_code ON movie_rooms(room_code);
CREATE INDEX idx_movie_rooms_creator_id ON movie_rooms(creator_id);
CREATE INDEX idx_movie_rooms_movie_id ON movie_rooms(movie_id);
CREATE INDEX idx_movie_rooms_is_private ON movie_rooms(is_private);
CREATE INDEX idx_movie_rooms_is_playing ON movie_rooms(is_playing);
CREATE INDEX idx_movie_room_members_room_id ON movie_room_members(room_id);
CREATE INDEX idx_movie_room_members_user_id ON movie_room_members(user_id);
CREATE INDEX idx_movie_room_members_is_online ON movie_room_members(is_online);
CREATE INDEX idx_movie_room_members_role ON movie_room_members(role);

-- 显示表结构
SHOW TABLES; 