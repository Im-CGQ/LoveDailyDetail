# 系统配置功能说明

## 功能概述

系统配置功能已重新设计，去掉了全局配置概念，改为用户独立配置模式，支持伴侣配置同步。

## 主要特性

### 1. 用户独立配置
- 每个用户都有自己独立的配置记录
- 所有配置都必须关联到具体的用户ID
- 不再支持全局配置（user_id为NULL的配置）

### 2. 伴侣配置同步
- 时间相关配置会在伴侣间自动同步：
  - 在一起时间 (`together_date`)
  - 纪念日列表 (`anniversary_dates`)
  - 下次见面日期 (`next_meeting_date`)
- 个人配置不会同步给伴侣：
  - 背景音乐自动播放 (`background_music_autoplay`)
  - 分享过期时间 (`share_expire_minutes`)
  - 看信背景音乐 (`letter_background_music`)

### 3. 同步机制
- 当用户修改时间相关配置时，后端会：
  1. 通过用户表查找伴侣ID
  2. 同时更新当前用户和伴侣的配置
  3. 确保双方配置保持一致

## 数据库结构

```sql
CREATE TABLE system_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_type VARCHAR(20) DEFAULT 'STRING' COMMENT '配置类型',
    description VARCHAR(255) COMMENT '配置描述',
    user_id BIGINT NOT NULL COMMENT '用户ID，不再支持全局配置',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_config (config_key, user_id)
);
```

## API接口

### 用户配置相关
- `GET /system-config/user/{userId}` - 获取用户配置列表
- `GET /system-config/user/{userId}/map` - 获取用户配置映射
- `POST /system-config/save` - 保存配置
- `DELETE /system-config/delete/{configKey}/{userId}` - 删除配置

### 特定配置接口
- `GET /system-config/together-date/{userId}` - 获取在一起时间
- `POST /system-config/together-date/{userId}` - 设置在一起时间（同步伴侣）
- `GET /system-config/anniversary-dates/{userId}` - 获取纪念日列表
- `POST /system-config/anniversary-dates/{userId}` - 设置纪念日列表（同步伴侣）
- `GET /system-config/next-meeting-date/{userId}` - 获取下次见面日期
- `POST /system-config/next-meeting-date/{userId}` - 设置下次见面日期（同步伴侣）
- `GET /system-config/background-music-autoplay/{userId}` - 获取背景音乐配置
- `POST /system-config/background-music-autoplay/{userId}` - 设置背景音乐配置
- `GET /system-config/share-expire-minutes/{userId}` - 获取分享过期时间
- `POST /system-config/share-expire-minutes/{userId}` - 设置分享过期时间

## 数据库迁移

执行以下脚本移除全局配置约束：

```bash
# Windows
remove-global-config-constraint.bat

# Linux/Mac
./remove-global-config-constraint.sh

# 手动执行
mysql -u root -p < database/remove_global_config_constraint.sql
```

## 注意事项

1. 执行迁移前请备份数据库
2. 迁移会删除所有全局配置记录
3. 确保MySQL服务正在运行
4. 确保数据库用户有足够权限
5. 迁移完成后，所有配置都需要关联到具体用户
