# 记录美好回忆的每一天 - 后端

## 项目介绍

这是"记录美好回忆的每一天"应用的后端服务，基于Spring Boot框架开发，提供RESTful API接口，支持日记的增删改查、文件上传等功能。

## 功能特性

- 🔐 用户认证和授权
- 📝 日记的CRUD操作
- 📅 按日期查询日记
- 📁 文件上传到阿里云OSS
- 🎵 支持图片、视频、音频文件
- 🔍 按年月查询日记
- 🛡️ 数据验证和安全防护

## 技术栈

- **框架**: Spring Boot 2.7.14
- **数据库**: MySQL 8.0
- **ORM**: Spring Data JPA
- **安全**: Spring Security
- **文件存储**: 阿里云OSS
- **认证**: JWT
- **构建工具**: Maven

## 快速开始

### 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 8.0+

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE love_diary DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行初始化脚本：
```sql
-- 参考 database/init.sql 文件
```

### 配置文件

修改 `application.yml` 中的配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/love_diary?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your-username
    password: your-password

aliyun:
  oss:
    endpoint: https://oss-cn-hangzhou.aliyuncs.com
    access-key-id: your-access-key-id
    access-key-secret: your-access-key-secret
    bucket-name: your-bucket-name
    url-prefix: https://your-bucket-name.oss-cn-hangzhou.aliyuncs.com/
```

### 启动应用

```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

或者直接运行主类 `LoveDiaryApplication.java`

## API接口

### 日记相关接口

#### 获取所有日记
```
GET /api/diaries
```

#### 根据ID获取日记
```
GET /api/diaries/{id}
```

#### 根据日期获取日记
```
GET /api/diaries/date/{date}
```

#### 根据年月获取日记
```
GET /api/diaries/month?year=2024&month=1
```

#### 创建日记
```
POST /api/diaries
Content-Type: application/json

{
  "title": "我们的第一次约会",
  "date": "2024-01-15",
  "description": "今天是我们第一次约会...",
  "images": ["url1", "url2"],
  "video": "video_url",
  "backgroundMusic": "music_url"
}
```

#### 更新日记
```
PUT /api/diaries/{id}
Content-Type: application/json

{
  "title": "更新的标题",
  "date": "2024-01-15",
  "description": "更新的描述..."
}
```

#### 删除日记
```
DELETE /api/diaries/{id}
```

#### 文件上传
```
POST /api/diaries/upload
Content-Type: multipart/form-data

file: 文件
type: 文件类型 (image/video/audio)
```

## 项目结构

```
src/main/java/com/lovediary/
├── LoveDiaryApplication.java    # 主启动类
├── controller/                  # 控制器层
│   └── DiaryController.java     # 日记控制器
├── service/                     # 服务层
│   ├── DiaryService.java        # 日记服务接口
│   ├── OssService.java          # OSS服务接口
│   └── impl/                    # 服务实现
│       ├── DiaryServiceImpl.java
│       └── OssServiceImpl.java
├── repository/                  # 数据访问层
│   ├── DiaryRepository.java     # 日记Repository
│   └── UserRepository.java      # 用户Repository
├── entity/                      # 实体类
│   ├── Diary.java              # 日记实体
│   └── User.java               # 用户实体
└── dto/                        # 数据传输对象
    ├── DiaryDTO.java           # 日记DTO
    └── LoginDTO.java           # 登录DTO
```

## 数据库设计

### 日记表 (diaries)
- `id`: 主键
- `title`: 标题
- `date`: 日期
- `description`: 描述
- `video_url`: 视频URL
- `background_music_url`: 背景音乐URL
- `created_at`: 创建时间
- `updated_at`: 更新时间

### 日记图片表 (diary_images)
- `diary_id`: 日记ID (外键)
- `image_url`: 图片URL

### 用户表 (users)
- `id`: 主键
- `username`: 用户名
- `password`: 密码 (加密)
- `display_name`: 显示名称
- `role`: 角色
- `created_at`: 创建时间

## 安全配置

### 默认用户
- 用户名: `admin`
- 密码: `admin123`

### JWT配置
- 密钥: `love-diary-secret-key-2024`
- 过期时间: 24小时

## 文件上传

### 支持的文件类型
- 图片: jpg, jpeg, png, gif, webp
- 视频: mp4, avi, mov, wmv
- 音频: mp3, wav, aac, ogg

### 文件大小限制
- 最大文件大小: 100MB
- 最大请求大小: 100MB

## 部署说明

### 打包
```bash
mvn clean package
```

### 运行JAR包
```bash
java -jar target/love-diary-backend-1.0.0.jar
```

### Docker部署
```dockerfile
FROM openjdk:8-jre-alpine
COPY target/love-diary-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 开发说明

### 代码规范
- 遵循阿里巴巴Java开发手册
- 使用Lombok简化代码
- 统一异常处理
- 接口文档注释

### 测试
```bash
# 运行单元测试
mvn test

# 运行集成测试
mvn verify
```

## 监控和日志

### 日志配置
- 日志级别: DEBUG
- 日志格式: 时间 [线程] 级别 类名 - 消息
- 输出: 控制台

### 健康检查
```
GET /actuator/health
```

## 许可证

MIT License 