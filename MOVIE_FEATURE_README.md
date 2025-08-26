# 一起看电影功能说明

## 功能概述

一起看电影功能允许用户上传电影文件，创建房间，与伴侣或朋友一起观看电影，并同步播放进度。

## 主要功能

### 1. 电影管理
- **上传电影**: 用户可以上传电影文件、封面图片，设置标题、描述和可见性
- **电影列表**: 查看所有可访问的电影（自己的、伴侣的、公开的）
- **电影详情**: 查看电影详细信息，包括时长、文件大小等
- **权限控制**: 私密电影只有自己和伴侣可以观看，公开电影所有人都可以观看

### 2. 房间功能
- **创建房间**: 为指定电影创建观看房间，生成6位数字房间码
- **加入房间**: 通过房间码加入其他人的房间
- **房间成员**: 显示房间内的所有成员及其在线状态
- **权限验证**: 只有有权限观看电影的用户才能加入房间

### 3. 同步播放
- **进度同步**: 房间内所有成员的播放进度自动同步
- **播放控制**: 任何成员都可以控制播放/暂停，其他成员会同步
- **进度条操作**: 点击进度条跳转到指定时间，所有成员同步
- **实时同步**: 每2秒同步一次播放状态，确保所有成员观看体验一致

## 技术实现

### 后端架构

#### 数据库设计
```sql
-- 电影表
CREATE TABLE movies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    cover_url VARCHAR(500),
    movie_url VARCHAR(500),
    file_name VARCHAR(255),
    is_public BOOLEAN DEFAULT FALSE,
    duration_minutes INT,
    file_size BIGINT,
    user_id BIGINT NOT NULL,
    partner_id BIGINT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 电影房间表
CREATE TABLE movie_rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_name VARCHAR(255) NOT NULL,
    room_code VARCHAR(10) NOT NULL UNIQUE,
    current_time DOUBLE DEFAULT 0.0,
    is_playing BOOLEAN DEFAULT FALSE,
    last_updated_by BIGINT NULL,
    last_updated_at TIMESTAMP NULL,
    movie_id BIGINT NOT NULL,
    creator_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 房间成员表
CREATE TABLE movie_room_members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    is_online BOOLEAN DEFAULT TRUE,
    last_seen TIMESTAMP NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 核心组件
- **MovieController**: 电影管理API
- **MovieRoomController**: 房间管理API
- **MovieService**: 电影业务逻辑
- **MovieRoomService**: 房间业务逻辑

### 前端架构

#### 页面结构
- **MovieList.vue**: 电影列表页面
- **MovieDetail.vue**: 电影详情页面
- **MovieRoom.vue**: 电影房间页面
- **JoinRoom.vue**: 加入房间页面

#### API接口
- **movie.js**: 电影相关API
- **movieRoom.js**: 房间相关API

## 使用流程

### 1. 上传电影
1. 进入电影列表页面
2. 点击"上传电影"按钮
3. 填写电影信息（标题、描述、是否公开）
4. 选择电影文件和封面图片
5. 提交上传

### 2. 创建房间
1. 在电影列表或详情页面点击"一起看"按钮
2. 系统自动创建房间并生成房间码
3. 进入房间页面，可以复制房间码分享给其他人

### 3. 加入房间
1. 点击"加入房间"或访问加入房间页面
2. 输入6位房间码
3. 系统验证权限后加入房间

### 4. 一起观看
1. 房间内所有成员看到相同的播放器
2. 任何成员操作播放控制都会同步给其他人
3. 进度条显示当前播放进度，可以点击跳转
4. 右侧显示房间成员列表

## 权限控制

### 电影权限
- **私密电影**: 只有创建者和其伴侣可以观看
- **公开电影**: 所有用户都可以观看
- **编辑权限**: 只有创建者可以编辑和删除电影

### 房间权限
- **创建房间**: 需要有权限观看该电影
- **加入房间**: 需要有权限观看该电影
- **房间控制**: 所有成员都可以控制播放，但只有创建者可以删除房间

## 同步机制

### 播放状态同步
- 每2秒从服务器获取最新播放状态
- 如果本地进度与服务器进度差异超过2秒，自动同步
- 用户操作播放控制时立即同步到服务器

### 成员状态同步
- 定期更新成员在线状态
- 显示成员加入时间和最后在线时间

## 文件上传

### 支持格式
- **电影文件**: mp4, avi, mov, wmv等主流视频格式
- **封面图片**: jpg, jpeg, png, gif, webp等图片格式

### 文件限制
- 最大文件大小: 100MB
- 文件存储在OSS云存储

## 部署说明

### 数据库更新
执行以下SQL脚本创建相关表：
```bash
mysql -u username -p database_name < database/update_movie_feature.sql
```

### 后端部署
1. 确保OSS配置正确
2. 重启后端服务
3. 验证API接口可用性

### 前端部署
1. 更新路由配置
2. 重新构建前端项目
3. 部署到服务器

## 注意事项

1. **网络要求**: 同步功能需要稳定的网络连接
2. **浏览器兼容**: 建议使用现代浏览器以获得最佳体验
3. **文件大小**: 大文件上传可能需要较长时间
4. **并发限制**: 单个房间建议不超过10人同时观看
5. **存储空间**: 注意监控OSS存储使用量

## 故障排除

### 常见问题
1. **同步延迟**: 检查网络连接，增加同步频率
2. **文件上传失败**: 检查文件格式和大小限制
3. **权限错误**: 确认用户关系和电影可见性设置
4. **房间无法加入**: 验证房间码是否正确，检查权限

### 日志查看
- 后端日志: 查看Spring Boot应用日志
- 前端日志: 打开浏览器开发者工具查看控制台

