-- 检查信件表数据
USE love_diary;

-- 查看信件表结构
DESCRIBE letters;

-- 查看所有信件数据
SELECT 
    id,
    title,
    SUBSTRING(content, 1, 50) as content_preview,
    unlock_time,
    sender_id,
    receiver_id,
    is_read,
    created_at,
    updated_at
FROM letters;

-- 统计信件数量
SELECT COUNT(*) as total_letters FROM letters;

-- 按发送者统计信件数量
SELECT 
    sender_id,
    COUNT(*) as sent_count
FROM letters 
GROUP BY sender_id;

-- 按接收者统计信件数量
SELECT 
    receiver_id,
    COUNT(*) as received_count
FROM letters 
GROUP BY receiver_id;

-- 查看用户表数据（用于验证用户ID）
SELECT id, username, display_name, partner_id FROM users;

-- 查看已解锁和未解锁的信件
SELECT 
    CASE 
        WHEN unlock_time <= NOW() THEN '已解锁'
        ELSE '未解锁'
    END as status,
    COUNT(*) as count
FROM letters 
GROUP BY 
    CASE 
        WHEN unlock_time <= NOW() THEN '已解锁'
        ELSE '未解锁'
    END;
