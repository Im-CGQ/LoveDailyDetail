-- 为chat_records表添加partner_id字段的迁移脚本

USE love_diary;

-- 添加partner_id字段
ALTER TABLE chat_records ADD COLUMN partner_id BIGINT NULL;

-- 添加外键约束
ALTER TABLE chat_records ADD CONSTRAINT fk_chat_records_partner_id 
FOREIGN KEY (partner_id) REFERENCES users(id) ON DELETE SET NULL;

-- 为现有记录设置partner_id
-- 根据用户的partner_id设置聊天记录的partner_id
UPDATE chat_records cr 
JOIN users u ON cr.user_id = u.id 
SET cr.partner_id = u.partner_id 
WHERE u.partner_id IS NOT NULL;

-- 创建索引
CREATE INDEX idx_chat_records_partner_id ON chat_records(partner_id);
CREATE INDEX idx_chat_records_user_partner ON chat_records(user_id, partner_id);
