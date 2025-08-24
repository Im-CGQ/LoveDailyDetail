-- 为diaries表添加伴侣字段的更新脚本
-- 这个脚本将为diaries表添加partner_id字段，用于实现伴侣关系功能

USE love_diary;

-- 为diaries表添加partner_id字段
ALTER TABLE diaries ADD COLUMN partner_id BIGINT NULL AFTER user_id;

-- 添加外键约束
ALTER TABLE diaries ADD CONSTRAINT fk_diaries_partner_id 
FOREIGN KEY (partner_id) REFERENCES users(id) ON DELETE SET NULL;

-- 创建索引以提高查询性能
CREATE INDEX idx_diaries_partner_id ON diaries(partner_id);
CREATE INDEX idx_diaries_user_partner ON diaries(user_id, partner_id);

-- 更新现有数据：如果用户有伴侣，则更新diaries表中的partner_id
UPDATE diaries d 
JOIN users u ON d.user_id = u.id 
SET d.partner_id = u.partner_id 
WHERE u.partner_id IS NOT NULL;

-- 显示更新结果
SELECT 
    'diaries表结构更新完成' as message,
    COUNT(*) as total_diaries,
    COUNT(partner_id) as diaries_with_partner
FROM diaries;
