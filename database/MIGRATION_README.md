# 数据库迁移说明：伴侣关系功能

## 概述

本次迁移为`diaries`表添加了`partner_id`字段，用于实现伴侣关系功能。这样可以让伴侣之间互相查看对方的回忆日记，但只有作者能够修改和删除。

## 迁移内容

### 1. 表结构变更

- 在`diaries`表中添加`partner_id`字段（BIGINT，可为空）
- 添加外键约束，关联到`users`表
- 创建索引以提高查询性能

### 2. 数据迁移

- 更新现有日记数据，如果用户有伴侣，则设置`partner_id`字段

## 执行步骤

### 1. 备份数据库

```bash
mysqldump -u your_username -p love_diary > love_diary_backup_$(date +%Y%m%d_%H%M%S).sql
```

### 2. 执行迁移脚本

```bash
mysql -u your_username -p love_diary < database/migrate_partner_field.sql
```

### 3. 验证迁移结果

```sql
-- 检查字段是否添加成功
DESCRIBE diaries;

-- 检查外键约束
SELECT 
    CONSTRAINT_NAME,
    COLUMN_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = 'love_diary' 
AND TABLE_NAME = 'diaries' 
AND COLUMN_NAME = 'partner_id';

-- 检查索引
SHOW INDEX FROM diaries WHERE Column_name IN ('partner_id', 'user_id');

-- 检查数据更新情况
SELECT 
    COUNT(*) as total_diaries,
    COUNT(partner_id) as diaries_with_partner,
    COUNT(*) - COUNT(partner_id) as diaries_without_partner
FROM diaries;
```

## 功能说明

### 1. 伴侣关系实现

- 当用户创建日记时，如果该用户有伴侣，系统会自动设置`partner_id`字段
- 伴侣可以查看对方的日记，但无法修改或删除
- 只有日记的作者可以修改和删除自己的日记

### 2. 查询逻辑

- **前台展示**：用户可以查看自己的日记和伴侣的日记
- **后台管理**：用户只能看到和管理自己创建的日记

### 3. 权限控制

- **查看权限**：`user_id = 当前用户ID` 或 `partner_id = 当前用户ID`
- **修改权限**：只有`user_id = 当前用户ID`的用户可以修改和删除

## 回滚方案

如果需要回滚，可以执行以下SQL：

```sql
-- 删除外键约束
ALTER TABLE diaries DROP FOREIGN KEY fk_diaries_partner_id;

-- 删除索引
DROP INDEX idx_diaries_partner_id ON diaries;
DROP INDEX idx_diaries_user_partner ON diaries;

-- 删除字段
ALTER TABLE diaries DROP COLUMN partner_id;
```

## 注意事项

1. 执行迁移前请务必备份数据库
2. 迁移过程中数据库服务可能会短暂不可用
3. 建议在低峰期执行迁移
4. 迁移完成后需要重启应用服务

## 测试建议

1. 创建两个用户并建立伴侣关系
2. 分别创建日记，验证伴侣可以看到对方的日记
3. 验证只有作者可以修改和删除日记
4. 验证后台管理只显示自己创建的日记
