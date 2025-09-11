-- 迁移脚本：将 diary_share_links 和 letter_share_links 合并为 universal_share_links
-- 执行前请先备份数据库！

-- 1. 创建新的通用分享链接表
CREATE TABLE universal_share_links (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    share_type VARCHAR(100) NOT NULL COMMENT '分享类型',
    target_id BIGINT NOT NULL COMMENT '目标ID（日记ID、信件ID等）',
    share_token VARCHAR(32) NOT NULL UNIQUE COMMENT '分享令牌',
    expires_at TIMESTAMP NOT NULL COMMENT '过期时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    view_count INT DEFAULT 0 COMMENT '查看次数',
    INDEX idx_share_type (share_type),
    INDEX idx_target_id (target_id),
    INDEX idx_share_token (share_token),
    INDEX idx_expires_at (expires_at),
    INDEX idx_is_active (is_active)
);

-- 2. 迁移 diary_share_links 数据
INSERT INTO universal_share_links (share_type, target_id, share_token, expires_at, created_at, is_active, view_count)
SELECT 
    'DIARY' as share_type,
    diary_id as target_id,
    share_token,
    expires_at,
    created_at,
    is_active,
    COALESCE(view_count, 0) as view_count
FROM diary_share_links;

-- 3. 迁移 letter_share_links 数据
INSERT INTO universal_share_links (share_type, target_id, share_token, expires_at, created_at, is_active, view_count)
SELECT 
    'LETTER' as share_type,
    letter_id as target_id,
    share_token,
    expires_at,
    created_at,
    is_active,
    COALESCE(view_count, 0) as view_count
FROM letter_share_links;

-- 4. 验证数据迁移
SELECT 
    share_type,
    COUNT(*) as count
FROM universal_share_links 
GROUP BY share_type;

-- 5. 备份原表（可选，建议保留一段时间）
-- RENAME TABLE diary_share_links TO diary_share_links_backup;
-- RENAME TABLE letter_share_links TO letter_share_links_backup;

-- 6. 删除原表（确认数据迁移无误后执行）
-- DROP TABLE diary_share_links;
-- DROP TABLE letter_share_links;
