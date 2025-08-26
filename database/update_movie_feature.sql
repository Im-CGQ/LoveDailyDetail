USE love_diary;

-- 电影功能数据库更新脚本
-- 执行时间: 2024年

-- 删除已存在的表（如果存在）
DROP TABLE IF EXISTS movie_room_members;
DROP TABLE IF EXISTS movie_rooms;
DROP TABLE IF EXISTS movies;

-- 创建电影表
CREATE TABLE movies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL COMMENT '电影标题',
    description TEXT COMMENT '电影描述',
    cover_url VARCHAR(500) COMMENT '封面图片URL',
    movie_url VARCHAR(500) COMMENT '电影文件URL',
    file_name VARCHAR(255) COMMENT '文件名',
    is_public BOOLEAN DEFAULT FALSE COMMENT '是否公开',
    duration_minutes INT COMMENT '电影时长（分钟）',
    file_size BIGINT COMMENT '文件大小（字节）',
    width INT COMMENT '视频宽度',
    height INT COMMENT '视频高度',
    user_id BIGINT NOT NULL COMMENT '创建者用户ID',
    partner_id BIGINT NULL COMMENT '伴侣用户ID',
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
    FOREIGN KEY (room_id) REFERENCES movie_rooms(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_room_user (room_id, user_id)
);

-- 创建索引
CREATE INDEX idx_movies_user_id ON movies(user_id);
CREATE INDEX idx_movies_partner_id ON movies(partner_id);
CREATE INDEX idx_movies_is_public ON movies(is_public);
CREATE INDEX idx_movies_created_at ON movies(created_at);
CREATE INDEX idx_movie_rooms_room_code ON movie_rooms(room_code);
CREATE INDEX idx_movie_rooms_creator_id ON movie_rooms(creator_id);
CREATE INDEX idx_movie_rooms_movie_id ON movie_rooms(movie_id);
CREATE INDEX idx_movie_room_members_room_id ON movie_room_members(room_id);
CREATE INDEX idx_movie_room_members_user_id ON movie_room_members(user_id);
CREATE INDEX idx_movie_room_members_is_online ON movie_room_members(is_online);

