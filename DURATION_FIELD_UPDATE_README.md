# 电影时长字段更新说明

## 问题描述
之前的电影时长存储使用 `duration_minutes` 字段（分钟），导致无法精确存储秒数信息。
例如：5分30秒的视频只能存储为5分钟，显示时变成5:00而不是5:30。

## 解决方案
添加 `duration` 字段（秒）来精确存储时长，同时保留 `duration_minutes` 字段以保持向后兼容。

## 更新步骤

### 1. 更新数据库
执行以下命令之一：

**Windows:**
```bash
update-database-duration.bat
```

**Linux/Mac:**
```bash
chmod +x update-database-duration.sh
./update-database-duration.sh
```

**手动执行SQL:**
```sql
USE love_diary;
ALTER TABLE movies ADD COLUMN duration INT NULL COMMENT '时长（秒）' AFTER duration_minutes;
CREATE INDEX idx_movies_duration ON movies(duration);
```

### 2. 后端代码已更新
- ✅ `MovieDTO.java` - 添加了 `durationSeconds` 字段
- ✅ `Movie.java` - 添加了 `durationSeconds` 字段，映射到数据库 `duration` 列

### 3. 前端代码已更新
- ✅ `CreateMovie.vue` - 发送 `durationSeconds` 字段
- ✅ `EditMovie.vue` - 发送 `durationSeconds` 字段
- ✅ `MovieList.vue` - 显示 `durationSeconds` 字段
- ✅ `MovieDetail.vue` - 显示 `durationSeconds` 字段

## 数据流程

### 上传视频
```
前端获取时长(秒) → 发送 durationSeconds → 后端存储到 duration 字段
```

### 显示视频
```
后端返回 duration 字段 → 前端直接显示为 分:秒 格式
```

## 字段说明

| 字段名 | 类型 | 说明 | 示例 |
|--------|------|------|------|
| `duration_minutes` | INT | 时长（分钟）- 向后兼容 | 5 |
| `duration` | INT | 时长（秒）- 精确存储 | 330 |

## 显示格式
- **5分钟视频**：显示为 `5:00`
- **5分30秒视频**：显示为 `5:30`
- **1小时5分钟**：显示为 `1:05:00`

## 注意事项
1. 执行数据库更新脚本前请备份数据库
2. 新字段 `duration` 为可选字段，不会影响现有数据
3. 建议逐步迁移现有数据到新字段
4. 前端代码已完全兼容新字段

## 测试
更新完成后，请测试：
1. 创建新电影，时长包含秒数
2. 编辑现有电影，更新时长
3. 电影列表和详情页面正确显示时长
