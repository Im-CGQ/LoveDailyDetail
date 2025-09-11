# 通用分享链接系统

## 概述

本系统将原有的 `diary_share_links` 和 `letter_share_links` 两个表合并为一个通用的 `universal_share_links` 表，通过 `share_type` 字段来区分不同的分享类型，方便以后扩展其他类型的分享功能。

## 数据库结构

### 新的通用分享链接表

```sql
CREATE TABLE universal_share_links (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    share_type ENUM('DIARY', 'LETTER', 'MOVIE', 'CHAT_RECORD', 'MUSIC') NOT NULL COMMENT '分享类型',
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
```

## 支持的分享类型

- `DIARY`: 日记分享
- `LETTER`: 信件分享
- `MOVIE`: 电影分享（预留）
- `CHAT_RECORD`: 聊天记录分享（预留）
- `MUSIC`: 音乐分享（预留）

## API 接口

### 通用分享接口

#### 1. 创建分享链接
```
POST /api/share/create/{shareType}/{targetId}
Authorization: Bearer {token} (可选)
```

**参数：**
- `shareType`: 分享类型（DIARY, LETTER, MOVIE, CHAT_RECORD, MUSIC）
- `targetId`: 目标对象ID

**响应：**
```json
{
  "success": true,
  "message": "创建分享链接成功",
  "data": {
    "shareUrl": "/api/share/diary/abc123",
    "shareToken": "abc123",
    "expiresAt": "2024-01-01T12:00:00",
    "shareType": "DIARY"
  }
}
```

#### 2. 查看分享内容
```
GET /api/share/{shareType}/{shareToken}
```

**参数：**
- `shareType`: 分享类型
- `shareToken`: 分享令牌

#### 3. 验证分享链接
```
GET /api/share/validate/{shareType}/{shareToken}
```

#### 4. 清理过期链接
```
POST /api/share/cleanup?shareType={shareType}
```

### 兼容旧API

为了保持向后兼容，原有的API仍然可用：

#### 日记分享
- `POST /share/create/{diaryId}` - 创建日记分享链接
- `GET /share/diary/{shareToken}` - 查看分享的日记
- `GET /share/validate/{shareToken}` - 验证日记分享链接

#### 信件分享
- `POST /share/letter/create/{letterId}` - 创建信件分享链接
- `GET /share/letter/{shareToken}` - 查看分享的信件

## 使用示例

### 1. 创建日记分享链接

```java
// 使用通用服务
UniversalShareLink shareLink = universalShareService.createShareLink(
    diaryId, 
    UniversalShareLink.ShareType.DIARY, 
    userId
);

// 或使用兼容的旧API
ShareLink shareLink = shareService.createShareLink(diaryId, userId);
```

### 2. 获取分享的日记

```java
// 使用通用服务
SharedDiaryDTO diary = (SharedDiaryDTO) universalShareService.getTargetByShareToken(
    shareToken, 
    UniversalShareLink.ShareType.DIARY
);

// 或使用兼容的旧API
SharedDiaryDTO diary = shareService.getDiaryByShareToken(shareToken);
```

### 3. 扩展新的分享类型

要添加新的分享类型（如电影分享），只需：

1. 在 `UniversalShareLink.ShareType` 枚举中添加新类型
2. 在 `UniversalShareServiceImpl.validateTargetExists()` 方法中添加验证逻辑
3. 在 `UniversalShareServiceImpl.getTargetByShareToken()` 方法中添加处理逻辑
4. 创建相应的DTO类

## 数据库迁移

执行 `database/migrate_to_universal_share_links.sql` 脚本来迁移现有数据：

```bash
mysql -u username -p database_name < database/migrate_to_universal_share_links.sql
```

## 优势

1. **统一管理**: 所有分享链接都在一个表中，便于管理和维护
2. **易于扩展**: 添加新的分享类型只需修改枚举和少量代码
3. **向后兼容**: 保持原有API不变，现有功能不受影响
4. **性能优化**: 统一的索引和查询优化
5. **统计功能**: 可以轻松统计所有类型的分享数据

## 注意事项

1. 迁移前请先备份数据库
2. 迁移后建议保留原表一段时间作为备份
3. 新的分享类型需要相应的业务逻辑支持
4. 确保所有相关的Repository和Service都已更新
