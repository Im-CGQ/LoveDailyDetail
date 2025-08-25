-- 数据库更新脚本：添加分享功能
-- 执行时间：建议在维护时间执行
-- 影响：添加新的分享链接表，不影响现有数据

USE love_diary;

-- 检查表是否已存在，如果存在则跳过创建
-- 创建日记分享链接表
CREATE TABLE IF NOT EXISTS diary_share_links (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    diary_id BIGINT NOT NULL,
    share_token VARCHAR(32) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (diary_id) REFERENCES diaries(id) ON DELETE CASCADE
);

-- 创建信件分享链接表
CREATE TABLE IF NOT EXISTS letter_share_links (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    letter_id BIGINT NOT NULL,
    share_token VARCHAR(32) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (letter_id) REFERENCES letters(id) ON DELETE CASCADE
);

CREATE INDEX idx_diary_share_links_token ON diary_share_links(share_token);
CREATE INDEX idx_diary_share_links_diary_id ON diary_share_links(diary_id);
CREATE INDEX idx_diary_share_links_expires_at ON diary_share_links(expires_at);
CREATE INDEX idx_letter_share_links_token ON letter_share_links(share_token);
CREATE INDEX idx_letter_share_links_letter_id ON letter_share_links(letter_id);
CREATE INDEX idx_letter_share_links_expires_at ON letter_share_links(expires_at); 

-- 验证表创建成功
SELECT 'diary_share_links' as table_name, COUNT(*) as record_count FROM diary_share_links
UNION ALL
SELECT 'letter_share_links' as table_name, COUNT(*) as record_count FROM letter_share_links;

-- 显示表结构
DESCRIBE diary_share_links;
DESCRIBE letter_share_links;
