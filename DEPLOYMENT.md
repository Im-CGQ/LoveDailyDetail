# 部署指南

## 环境要求

### 必需软件
- **Java 8+** - 运行Spring Boot应用
- **Node.js 16+** - 运行Vue前端
- **MySQL 8.0+** - 数据库
- **Maven 3.6+** - 构建Java项目

### 可选软件
- **Git** - 版本控制
- **IDE** - IntelliJ IDEA / Eclipse / VS Code

## 快速部署

### 1. 环境检查
```bash
# 检查Java版本
java -version

# 检查Node.js版本
node -v

# 检查MySQL版本
mysql --version

# 检查Maven版本
mvn -version
```

### 2. 数据库初始化
```bash
# 启动MySQL服务
# Windows: net start mysql
# Linux: sudo systemctl start mysql

# 运行初始化脚本
双击运行 init-database.bat
```

### 3. 启动服务
```bash
# 启动后端（新开一个终端）
双击运行 start-backend.bat

# 启动前端（新开一个终端）
双击运行 start.bat
```

### 4. 验证部署
- 前端: http://localhost:3000
- 后端API: http://localhost:8080/api
- API测试: 打开 test-api.html

## 详细配置

### 数据库配置
编辑 `backend/src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/love_diary?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 阿里云OSS配置（可选）
```yaml
aliyun:
  oss:
    endpoint: https://oss-cn-hangzhou.aliyuncs.com
    access-key-id: your-access-key-id
    access-key-secret: your-access-key-secret
    bucket-name: your-bucket-name
    url-prefix: https://your-bucket-name.oss-cn-hangzhou.aliyuncs.com/
```

### 前端配置
编辑 `frontend/.env`:
```env
VITE_API_URL=http://localhost:8080/api
```

## 生产环境部署

### 后端部署
```bash
# 打包
cd backend
mvn clean package -DskipTests

# 运行
java -jar target/love-diary-backend-1.0.0.jar
```

### 前端部署
```bash
# 构建
cd frontend
npm run build

# 部署到Web服务器
# 将 dist 目录内容复制到Web服务器根目录
```

### Docker部署
```bash
# 构建后端镜像
cd backend
docker build -t love-diary-backend .

# 运行后端容器
docker run -d -p 8080:8080 --name love-diary-backend love-diary-backend

# 构建前端镜像
cd frontend
docker build -t love-diary-frontend .

# 运行前端容器
docker run -d -p 80:80 --name love-diary-frontend love-diary-frontend
```

## 故障排除

### 常见问题

#### 1. 数据库连接失败
- 检查MySQL服务是否启动
- 验证数据库连接信息
- 确认数据库用户权限

#### 2. 前端无法访问后端API
- 检查后端服务是否启动
- 验证API地址配置
- 检查防火墙设置

#### 3. 端口被占用
```bash
# Windows查看端口占用
netstat -ano | findstr :8080

# Linux查看端口占用
lsof -i :8080
```

#### 4. 内存不足
```bash
# 增加JVM内存
java -Xmx2g -jar love-diary-backend-1.0.0.jar
```

### 日志查看
```bash
# 后端日志
tail -f backend/logs/application.log

# 前端日志
# 查看浏览器控制台
```

## 性能优化

### 数据库优化
- 添加适当的索引
- 优化查询语句
- 配置连接池

### 前端优化
- 启用Gzip压缩
- 使用CDN加速
- 优化图片大小

### 后端优化
- 配置JVM参数
- 启用缓存
- 优化数据库查询

## 安全配置

### 生产环境安全
- 修改默认密码
- 配置HTTPS
- 设置防火墙规则
- 定期备份数据

### 数据库安全
- 限制数据库访问IP
- 使用强密码
- 定期更新MySQL版本

## 监控和维护

### 健康检查
- 定期检查服务状态
- 监控系统资源使用
- 查看错误日志

### 备份策略
- 数据库定期备份
- 配置文件备份
- 代码版本管理

### 更新部署
- 制定更新计划
- 测试环境验证
- 灰度发布策略 