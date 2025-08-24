-- 测试伴侣功能脚本
-- 执行迁移后运行此脚本来验证功能

USE love_diary;

-- 1. 创建测试用户
INSERT INTO users (username, password, display_name, role) VALUES 
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '用户1', 'USER'),
('user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '用户2', 'USER');

-- 2. 建立伴侣关系
UPDATE users SET partner_id = 3 WHERE id = 2; -- user1的伴侣是user2
UPDATE users SET partner_id = 2 WHERE id = 3; -- user2的伴侣是user1

-- 3. 创建测试日记
INSERT INTO diaries (title, date, description, user_id, partner_id) VALUES 
('用户1的日记1', '2024-01-01', '这是用户1创建的日记1', 2, 3),
('用户1的日记2', '2024-01-02', '这是用户1创建的日记2', 2, 3),
('用户2的日记1', '2024-01-03', '这是用户2创建的日记1', 3, 2),
('用户2的日记2', '2024-01-04', '这是用户2创建的日记2', 3, 2);

-- 4. 验证数据
SELECT '用户1的日记' as test_case, COUNT(*) as count FROM diaries WHERE user_id = 2;
SELECT '用户2的日记' as test_case, COUNT(*) as count FROM diaries WHERE user_id = 3;
SELECT '用户1可以查看的日记' as test_case, COUNT(*) as count FROM diaries WHERE user_id = 2 OR partner_id = 2;
SELECT '用户2可以查看的日记' as test_case, COUNT(*) as count FROM diaries WHERE user_id = 3 OR partner_id = 3;

-- 5. 显示所有日记及其作者和伴侣信息
SELECT 
    d.id,
    d.title,
    d.date,
    u1.username as author,
    u2.username as partner,
    CASE 
        WHEN d.user_id = 2 THEN '用户1创建'
        WHEN d.user_id = 3 THEN '用户2创建'
        ELSE '其他用户创建'
    END as diary_type
FROM diaries d
JOIN users u1 ON d.user_id = u1.id
LEFT JOIN users u2 ON d.partner_id = u2.id
ORDER BY d.date;

-- 6. 清理测试数据（可选）
-- DELETE FROM diaries WHERE user_id IN (2, 3);
-- DELETE FROM users WHERE id IN (2, 3);
