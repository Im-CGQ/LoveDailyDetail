# LoveDaily 项目文件清单

## 📦 完整项目结构

```
LoveDailyDetail/
├── 📁 frontend/                    # 前端项目 (Vue.js + Vant)
│   ├── 📄 package.json            # 项目依赖配置
│   ├── 📄 vite.config.js          # Vite构建配置
│   ├── 📄 index.html              # 入口HTML文件
│   ├── 📁 src/                    # 源代码目录
│   │   ├── 📁 api/                # API接口文件
│   │   │   ├── auth.js            # 认证相关API
│   │   │   ├── diary.js           # 日记相关API
│   │   │   ├── letter.js          # 信件相关API
│   │   │   ├── partner.js         # 伴侣相关API
│   │   │   └── chatRecord.js      # 聊天记录API
│   │   ├── 📁 views/              # 页面组件
│   │   │   ├── Welcome.vue        # 欢迎页面
│   │   │   ├── Home.vue           # 首页
│   │   │   ├── Calendar.vue       # 日历页面
│   │   │   ├── Detail.vue         # 详情页面
│   │   │   ├── LetterBox.vue      # 信箱页面
│   │   │   ├── ChatRecord.vue     # 聊天记录页面
│   │   │   ├── Login.vue          # 登录页面
│   │   │   ├── Register.vue       # 注册页面
│   │   │   └── 📁 admin/          # 后台管理页面
│   │   │       ├── AdminLayout.vue
│   │   │       ├── AdminHome.vue
│   │   │       ├── CreateDiary.vue
│   │   │       ├── EditDiary.vue
│   │   │       ├── DiaryList.vue
│   │   │       ├── WriteLetter.vue
│   │   │       ├── LetterList.vue
│   │   │       ├── ChatRecordList.vue
│   │   │       ├── CreateChatRecord.vue
│   │   │       └── EditChatRecord.vue
│   │   ├── 📁 router/             # 路由配置
│   │   │   └── index.js           # 路由定义
│   │   ├── 📁 stores/             # 状态管理
│   │   │   └── user.js            # 用户状态
│   │   ├── 📁 utils/              # 工具函数
│   │   │   └── auth.js            # 认证工具
│   │   ├── 📁 styles/             # 样式文件
│   │   ├── 📁 assets/             # 静态资源
│   │   ├── App.vue                # 根组件
│   │   └── main.js                # 入口文件
│   └── 📁 public/                 # 公共资源
│
├── 📁 backend/                     # 后端项目 (Spring Boot)
│   ├── 📄 pom.xml                 # Maven配置
│   ├── 📄 application.yml         # 应用配置
│   ├── 📁 src/main/java/          # Java源代码
│   │   └── com/lovediary/         # 包结构
│   │       ├── 📁 entity/         # 实体类
│   │       │   ├── User.java      # 用户实体
│   │       │   ├── Diary.java     # 日记实体
│   │       │   ├── Letter.java    # 信件实体
│   │       │   ├── Partner.java   # 伴侣实体
│   │       │   └── ChatRecord.java # 聊天记录实体
│   │       ├── 📁 dto/            # 数据传输对象
│   │       │   ├── UserDTO.java
│   │       │   ├── DiaryDTO.java
│   │       │   ├── LetterDTO.java
│   │       │   └── ChatRecordDTO.java
│   │       ├── 📁 repository/     # 数据访问层
│   │       │   ├── UserRepository.java
│   │       │   ├── DiaryRepository.java
│   │       │   ├── LetterRepository.java
│   │       │   └── ChatRecordRepository.java
│   │       ├── 📁 service/        # 业务逻辑层
│   │       │   ├── UserService.java
│   │       │   ├── DiaryService.java
│   │       │   ├── LetterService.java
│   │       │   ├── PartnerService.java
│   │       │   ├── ChatRecordService.java
│   │       │   └── 📁 impl/       # 服务实现
│   │       │       ├── UserServiceImpl.java
│   │       │       ├── DiaryServiceImpl.java
│   │       │       ├── LetterServiceImpl.java
│   │       │       ├── PartnerServiceImpl.java
│   │       │       └── ChatRecordServiceImpl.java
│   │       ├── 📁 controller/     # 控制器层
│   │       │   ├── UserController.java
│   │       │   ├── DiaryController.java
│   │       │   ├── LetterController.java
│   │       │   ├── PartnerController.java
│   │       │   ├── ChatRecordController.java
│   │       │   └── AdminController.java
│   │       ├── 📁 config/         # 配置类
│   │       │   ├── SecurityConfig.java
│   │       │   └── CorsConfig.java
│   │       ├── 📁 util/           # 工具类
│   │       │   ├── JwtUtil.java
│   │       │   └── ResponseUtil.java
│   │       └── LoveDiaryApplication.java # 启动类
│   └── 📁 src/main/resources/     # 资源文件
│       └── application.yml        # 配置文件
│
├── 📁 docker/                      # Docker部署配置
│   ├── 📁 frontend/               # 前端Docker配置
│   │   ├── 📄 Dockerfile          # 前端容器构建文件
│   │   └── 📄 nginx.conf          # Nginx配置文件
│   ├── 📁 backend/                # 后端Docker配置
│   │   └── 📄 Dockerfile          # 后端容器构建文件
│   ├── 📄 docker-compose.yml      # 服务编排文件
│   ├── 📄 .dockerignore           # Docker忽略文件
│   └── 📄 README.md               # Docker部署说明
│
├── 📁 database/                    # 数据库相关
│   └── 📄 init.sql                # 数据库初始化脚本
│
├── 📄 deploy.sh                    # 一键部署脚本
├── 📄 DEPLOYMENT_GUIDE.md         # 详细部署指南
├── 📄 PROJECT_FILES.md            # 项目文件清单
└── 📄 README.md                   # 项目说明文档
```

## 📋 必需文件清单

### ✅ 核心文件（必须上传）
- [ ] `frontend/package.json` - 前端依赖配置
- [ ] `frontend/vite.config.js` - 前端构建配置
- [ ] `frontend/src/` - 前端源代码目录
- [ ] `backend/pom.xml` - 后端依赖配置
- [ ] `backend/application.yml` - 后端配置文件
- [ ] `backend/src/` - 后端源代码目录
- [ ] `docker/docker-compose.yml` - Docker编排文件
- [ ] `docker/frontend/Dockerfile` - 前端容器构建
- [ ] `docker/backend/Dockerfile` - 后端容器构建
- [ ] `docker/frontend/nginx.conf` - Nginx配置
- [ ] `database/init.sql` - 数据库初始化脚本

### 📄 文档文件（建议上传）
- [ ] `README.md` - 项目说明
- [ ] `DEPLOYMENT_GUIDE.md` - 部署指南
- [ ] `PROJECT_FILES.md` - 文件清单
- [ ] `docker/README.md` - Docker说明
- [ ] `deploy.sh` - 一键部署脚本

### 🗂️ 可选文件
- [ ] `.gitignore` - Git忽略文件
- [ ] `frontend/.env` - 前端环境变量
- [ ] `backend/logs/` - 后端日志目录

## 🚀 快速部署检查

### 1. 文件完整性检查
```bash
# 检查关键文件是否存在
ls -la frontend/package.json
ls -la backend/pom.xml
ls -la docker/docker-compose.yml
ls -la database/init.sql
```

### 2. 环境检查
```bash
# 检查Docker
docker --version
docker-compose --version

# 检查端口占用
netstat -tulpn | grep :80
netstat -tulpn | grep :40000
```

### 3. 一键部署
```bash
# 给部署脚本执行权限
chmod +x deploy.sh

# 运行部署脚本
./deploy.sh
```

## 📦 打包上传

### 方式一：Git仓库
```bash
# 克隆项目
git clone <repository-url>
cd LoveDailyDetail
./deploy.sh
```

### 方式二：压缩包上传
```bash
# 打包项目
tar -czf LoveDailyDetail.tar.gz ./

# 上传到服务器
scp LoveDailyDetail.tar.gz user@server:/path/to/

# 在服务器上解压
tar -xzf LoveDailyDetail.tar.gz
cd LoveDailyDetail
./deploy.sh
```

### 方式三：直接上传目录
```bash
# 使用rsync上传
rsync -avz LoveDailyDetail/ user@server:/path/to/LoveDailyDetail/

# 或使用scp
scp -r LoveDailyDetail/ user@server:/path/to/
```

## 🔧 配置文件说明

### 前端配置要点
- `package.json`: 包含所有依赖和构建脚本
- `vite.config.js`: 配置构建输出和代理
- `src/api/`: 所有API接口文件
- `src/router/index.js`: 路由配置

### 后端配置要点
- `pom.xml`: Maven依赖和构建配置
- `application.yml`: 数据库连接和应用配置
- `src/main/java/`: 所有Java源代码

### Docker配置要点
- `docker-compose.yml`: 服务编排和网络配置
- `frontend/Dockerfile`: 前端多阶段构建
- `backend/Dockerfile`: 后端Java应用构建
- `frontend/nginx.conf`: Nginx代理和静态文件服务

## 🎯 部署成功标志

部署成功后，你应该能够：
1. 访问 http://localhost 看到前端页面
2. 访问 http://localhost:40000/api 看到后端API
3. 使用 `docker-compose ps` 看到所有服务正常运行
4. 使用 `docker-compose logs` 查看无错误日志

## 📞 技术支持

如果遇到问题：
1. 检查 `DEPLOYMENT_GUIDE.md` 中的故障排除部分
2. 查看 `docker/README.md` 中的详细说明
3. 运行 `docker-compose logs` 查看错误信息
4. 确认所有必需文件都已正确上传
