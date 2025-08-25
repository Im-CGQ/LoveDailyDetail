# 记录美好回忆的每一天 💕

一个浪漫的移动端应用，用于记录与女朋友的每一个美好瞬间。采用Vue3 + Spring Boot技术栈，具有现代化的UI设计和丰富的动画效果。

## ✨ 功能特性

### 前端展示
- 📱 移动端友好的响应式设计
- 📅 时光日历，标记有回忆的日期
- 🖼️ 支持图片、视频、音频展示
- 📝 文字描述和标题
- ✨ 丰富的动画效果和渐变
- 💕 浪漫的视觉设计

### 后台管理
- 🔐 管理员登录系统
- 📝 回忆创建、编辑、删除
- 📁 文件上传（图片、视频、音频）
- 📊 数据统计和管理

## 🛠️ 技术栈

### 前端
- **框架**: Vue 3 + Composition API
- **UI组件库**: Vant 4
- **路由**: Vue Router 4
- **状态管理**: Pinia
- **构建工具**: Vite
- **样式**: SCSS
- **日期处理**: Day.js
- **HTTP客户端**: Axios

### 后端
- **框架**: Spring Boot 2.7.14
- **数据库**: MySQL 8.0
- **ORM**: Spring Data JPA
- **安全**: Spring Security
- **文件存储**: 阿里云OSS
- **认证**: JWT
- **构建工具**: Maven

## ✨ 项目特性

### 前端特性
- 🎨 **浪漫动感UI** - 渐变背景、动画效果、爱心装饰
- 📱 **移动端优先** - 响应式设计，完美适配手机
- 💫 **动态效果** - 飘落花瓣、打字机效果、实时计时器
- 📅 **双视图日历** - 列表视图和日历视图切换
- 🔐 **统一登录** - 用户和管理员共用登录页面
- 🖼️ **图片预览** - 点击图片可全屏预览
- 💾 **持久化登录** - 支持一个月内免登录
- 🔄 **Mock数据备用** - 后端未启动时自动使用Mock数据

### 后端特性
- 🔒 **JWT认证** - 安全的身份验证机制
- 📊 **自动初始化** - 启动时自动创建默认用户和示例数据
- 🌐 **跨域支持** - 完整的前后端分离支持
- 📝 **RESTful API** - 标准的REST接口设计
- 🗄️ **数据持久化** - MySQL数据库存储

## 🚀 快速开始

### 环境要求
- Node.js >= 16
- JDK 8+
- Maven 3.6+
- MySQL 8.0+

### 1. 克隆项目
```bash
git clone <repository-url>
cd love-diary
```

### 2. 数据库配置
```bash
# 方法1：使用初始化脚本（推荐）
双击运行 init-database.bat

# 方法2：手动执行SQL
mysql -u root -p < backend/database/init.sql
```

### 3. 后端配置
```bash
cd backend
# 修改 application.yml 中的数据库和OSS配置
mvn clean compile
mvn spring-boot:run
```

### 4. 前端配置
```bash
cd frontend
npm install
npm run dev
```

### 5. 一键启动（Windows）
```bash
# 方法1：分别启动
# 启动前端：双击运行 start.bat
# 启动后端：双击运行 start-backend.bat

# 方法2：Docker一键启动（推荐）
# 双击运行 start-docker.bat
```

### 6. 访问地址
- 前端: http://localhost:3000
- 后端API: http://localhost:8080/api
- 默认账号: admin / admin

### 7. 数据说明
- 后端启动时会自动创建默认管理员账号和示例日记数据
- 如果后端未启动，前端会自动使用Mock数据，确保功能正常

### 8. 测试API
```bash
# 在浏览器中打开 test-api.html 文件
# 可以测试所有后端API接口
```

## 📁 项目结构

```
love-diary/
├── frontend/                 # 前端项目
│   ├── src/
│   │   ├── components/      # 公共组件
│   │   ├── views/           # 页面组件
│   │   │   ├── Home.vue     # 首页
│   │   │   ├── Calendar.vue # 日历页面
│   │   │   ├── Detail.vue   # 详情页面
│   │   │   ├── Login.vue    # 登录页面
│   │   │   ├── Welcome.vue  # 欢迎页面
│   │   │   └── admin/       # 后台管理页面
│   │   ├── router/          # 路由配置
│   │   ├── api/             # API接口
│   │   ├── utils/           # 工具函数
│   │   └── styles/          # 样式文件
│   ├── Dockerfile           # Docker镜像配置
│   ├── nginx.conf           # Nginx配置
│   └── package.json
├── backend/                  # 后端项目
│   ├── src/main/java/
│   │   └── com/lovediary/
│   │       ├── controller/  # 控制器层
│   │       ├── service/     # 服务层
│   │       ├── repository/  # 数据访问层
│   │       ├── entity/      # 实体类
│   │       ├── dto/         # 数据传输对象
│   │       ├── config/      # 配置类
│   │       └── util/        # 工具类
│   ├── src/main/resources/
│   │   └── application.yml  # 配置文件
│   ├── database/
│   │   └── init.sql        # 数据库初始化脚本
│   ├── Dockerfile          # Docker镜像配置
│   └── pom.xml
├── start.bat                # 前端启动脚本
├── start-backend.bat        # 后端启动脚本
├── start-docker.bat         # Docker启动脚本
├── init-database.bat        # 数据库初始化脚本
├── docker-compose.yml       # Docker Compose配置
├── test-api.html           # API测试页面
└── README.md
│   ├── src/main/java/
│   │   └── com/lovediary/
│   │       ├── controller/  # 控制器
│   │       ├── service/     # 服务层
│   │       ├── repository/  # 数据访问层
│   │       ├── entity/      # 实体类
│   │       └── dto/         # 数据传输对象
│   └── pom.xml
├── database/                 # 数据库脚本
│   └── init.sql
└── README.md
```

## 🎨 设计特色

### 视觉设计
- **渐变色彩**: 采用粉色系渐变，营造浪漫氛围
- **毛玻璃效果**: 现代化的半透明背景
- **动画效果**: 心跳、飘落、悬浮等丰富动画
- **响应式布局**: 完美适配移动端和PC端

### 交互体验
- **流畅动画**: 页面切换和元素交互都有平滑动画
- **触觉反馈**: 按钮点击和悬停效果
- **直观导航**: 清晰的页面结构和导航

## 🔧 配置说明

### 前端配置
- `VITE_API_BASE_URL`: API基础地址
- `VITE_OSS_BASE_URL`: 阿里云OSS基础地址

### 后端配置
- 数据库连接配置
- 阿里云OSS配置
- JWT密钥配置

## 📱 页面说明

### 首页 (Home.vue)
- 展示最新的回忆
- 图片轮播展示
- 背景音乐播放
- 跳转到日历页面

### 日历页面 (Calendar.vue)
- 月份切换
- 日期标记（有回忆的日期显示爱心）
- 回忆列表展示
- 点击日期查看详情

### 详情页面 (Detail.vue)
- 完整的回忆内容展示
- 图片、视频、音频播放
- 分享功能
- 返回导航

### 后台管理
- 回忆创建、编辑、删除
- 文件上传（图片、视频、音频）
- 数据管理

## 🚀 部署说明

### 前端部署
```bash
cd frontend
npm run build
# 将 dist 目录部署到Web服务器
```

### 后端部署
```bash
cd backend
mvn clean package
java -jar target/love-diary-backend-1.0.0.jar
```

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 📄 许可证

MIT License

## 💝 特别感谢

感谢所有为这个项目做出贡献的朋友们！

---

**记录美好回忆的每一天** - 让每一个美好瞬间都成为永恒 💕 