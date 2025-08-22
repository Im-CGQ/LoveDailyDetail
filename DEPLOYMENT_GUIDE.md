# LoveDaily 项目部署指南

## 📋 部署前准备清单

### 必需文件清单
```
✅ frontend/                    # 前端项目目录
✅ backend/                     # 后端项目目录  
✅ docker/                      # Docker部署配置
✅ database/init.sql           # 数据库初始化脚本
✅ README.md                   # 项目说明文档
```

### 服务器环境要求
- **操作系统**: Linux (Ubuntu 20.04+ / CentOS 7+)
- **Docker**: 20.10+
- **Docker Compose**: 2.0+
- **内存**: 至少2GB可用内存
- **磁盘**: 至少5GB可用空间
- **网络**: 80端口和40000端口可访问

## 🚀 部署步骤

### 1. 上传项目文件

#### 方式一：Git克隆（推荐）
```bash
# 在服务器上克隆项目
git clone <项目仓库地址>
cd LoveDailyDetail
```

#### 方式二：文件上传
```bash
# 使用scp上传
scp -r LoveDailyDetail/ user@server:/path/to/project/

# 或使用rsync
rsync -avz LoveDailyDetail/ user@server:/path/to/project/
```

### 2. 检查文件完整性
```bash
# 检查关键文件是否存在
ls -la frontend/package.json
ls -la backend/pom.xml
ls -la docker/docker-compose.yml
ls -la database/init.sql
```

### 3. 配置环境变量（可选）
```bash
# 创建环境配置文件
cp docker/docker-compose.yml docker/docker-compose.prod.yml

# 编辑生产环境配置
vim docker/docker-compose.prod.yml
```

### 4. 启动服务
```bash
# 进入docker目录
cd docker

# 创建日志目录
mkdir -p logs/backend logs/nginx

# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps
```

### 5. 验证部署
```bash
# 检查服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 测试访问
curl http://localhost
curl http://localhost:40000/api/actuator/health
```

## 🔧 配置文件说明

### 1. 前端配置 (frontend/)
- `package.json`: 项目依赖和脚本
- `vite.config.js`: 构建配置
- `src/api/`: API接口配置
- `src/router/`: 路由配置

### 2. 后端配置 (backend/)
- `pom.xml`: Maven依赖管理
- `application.yml`: Spring Boot配置
- `src/main/resources/`: 配置文件目录

### 3. Docker配置 (docker/)
- `docker-compose.yml`: 服务编排
- `frontend/Dockerfile`: 前端容器构建
- `backend/Dockerfile`: 后端容器构建
- `frontend/nginx.conf`: Nginx配置

### 4. 数据库配置 (database/)
- `init.sql`: 数据库初始化脚本

## 🌐 访问地址

### 本地访问
- **前端应用**: http://localhost
- **后端API**: http://localhost:40000/api
- **数据库**: localhost:3306

### 服务器访问
- **前端应用**: http://服务器IP
- **后端API**: http://服务器IP:40000/api
- **数据库**: 服务器IP:3306

## 🔒 安全配置

### 1. 修改默认密码
```bash
# 编辑docker-compose.yml
vim docker/docker-compose.yml

# 修改数据库密码
MYSQL_ROOT_PASSWORD: your_secure_password
```

### 2. 配置防火墙
```bash
# Ubuntu/Debian
sudo ufw allow 80
sudo ufw allow 40000
sudo ufw enable

# CentOS/RHEL
sudo firewall-cmd --permanent --add-port=80/tcp
sudo firewall-cmd --permanent --add-port=40000/tcp
sudo firewall-cmd --reload
```

### 3. 配置SSL证书（生产环境）
```bash
# 安装Certbot
sudo apt install certbot python3-certbot-nginx

# 获取SSL证书
sudo certbot --nginx -d yourdomain.com
```

## 📊 监控和维护

### 1. 查看服务状态
```bash
# 查看所有容器状态
docker-compose ps

# 查看服务日志
docker-compose logs -f frontend
docker-compose logs -f backend
docker-compose logs -f mysql
```

### 2. 备份数据
```bash
# 备份数据库
docker-compose exec mysql mysqldump -u root -p love_diary > backup_$(date +%Y%m%d).sql

# 备份整个项目
tar -czf lovediary_backup_$(date +%Y%m%d).tar.gz ./
```

### 3. 更新部署
```bash
# 停止服务
docker-compose down

# 拉取最新代码
git pull

# 重新构建和启动
docker-compose up -d --build
```

## 🐛 故障排除

### 1. 常见问题

#### 端口被占用
```bash
# 检查端口占用
netstat -tulpn | grep :80
netstat -tulpn | grep :40000

# 修改端口映射
vim docker/docker-compose.yml
```

#### 数据库连接失败
```bash
# 检查数据库服务
docker-compose logs mysql

# 进入数据库容器
docker-compose exec mysql mysql -u root -p
```

#### 前端无法访问后端
```bash
# 检查nginx配置
docker-compose exec frontend cat /etc/nginx/nginx.conf

# 检查后端服务
curl http://backend:8080/api/actuator/health
```

### 2. 日志分析
```bash
# 查看实时日志
docker-compose logs -f

# 查看错误日志
docker-compose logs | grep ERROR

# 查看特定时间日志
docker-compose logs --since="2024-01-01T00:00:00"
```

## 📞 技术支持

### 联系信息
- **项目文档**: README.md
- **部署文档**: docker/README.md
- **问题反馈**: GitHub Issues

### 检查清单
- [ ] 所有必需文件已上传
- [ ] Docker和Docker Compose已安装
- [ ] 端口80和40000未被占用
- [ ] 服务器有足够内存和磁盘空间
- [ ] 网络连接正常
- [ ] 防火墙配置正确

## 🎯 快速部署命令

```bash
# 一键部署脚本
#!/bin/bash
cd docker
mkdir -p logs/backend logs/nginx
docker-compose up -d
echo "部署完成！访问 http://localhost"
```
