# LoveDaily Docker 部署指南

## 项目结构

```
docker/
├── frontend/          # 前端Docker配置
│   ├── Dockerfile     # 前端Dockerfile
│   └── nginx.conf     # Nginx配置文件
├── backend/           # 后端Docker配置
│   └── Dockerfile     # 后端Dockerfile
├── docker-compose.yml # Docker编排文件
├── .dockerignore      # Docker忽略文件
└── README.md         # 部署说明
```

## 服务端口

- **前端**: 80端口 (http://localhost)
- **后端**: 40000端口 (http://localhost:40000)
- **数据库**: 3306端口 (localhost:3306)

## 快速部署

### 1. 环境要求

- Docker 20.10+
- Docker Compose 2.0+
- 至少2GB可用内存

### 2. 部署步骤

```bash
# 1. 进入docker目录
cd docker

# 2. 创建必要的目录
mkdir -p logs/backend logs/nginx

# 3. 启动所有服务
docker-compose up -d

# 4. 查看服务状态
docker-compose ps

# 5. 查看日志
docker-compose logs -f
```

### 3. 访问应用

- 前端: http://localhost
- 后端API: http://localhost:40000
- 数据库: localhost:3306

## 服务管理

### 启动服务
```bash
docker-compose up -d
```

### 停止服务
```bash
docker-compose down
```

### 重启服务
```bash
docker-compose restart
```

### 查看日志
```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f frontend
docker-compose logs -f backend
docker-compose logs -f mysql
```

### 进入容器
```bash
# 进入前端容器
docker-compose exec frontend sh

# 进入后端容器
docker-compose exec backend sh

# 进入数据库容器
docker-compose exec mysql mysql -u lovediary -p
```

## 数据库配置

### 默认配置
- 数据库名: love_diary
- 用户名: root
- 密码: 123456

### 连接数据库
```bash
# 使用Docker Compose
docker-compose exec mysql mysql -u root -p love_diary

# 使用外部客户端
Host: localhost
Port: 3306
Database: love_diary
Username: root
Password: 123456
```

## 环境变量

### 后端环境变量
- `SPRING_PROFILES_ACTIVE`: 激活的Spring配置文件 (prod)
- `SPRING_DATASOURCE_URL`: 数据库连接URL
- `SPRING_DATASOURCE_USERNAME`: 数据库用户名
- `SPRING_DATASOURCE_PASSWORD`: 数据库密码
- `SERVER_PORT`: 服务端口 (40000)

### 数据库环境变量
- `MYSQL_ROOT_PASSWORD`: Root密码
- `MYSQL_DATABASE`: 数据库名
- `MYSQL_USER`: 用户名
- `MYSQL_PASSWORD`: 密码

## 数据持久化

### 数据库数据
数据库数据存储在Docker卷 `mysql_data` 中，重启容器数据不会丢失。

### 日志文件
- 后端日志: `./logs/backend/`
- Nginx日志: `./logs/nginx/`

## 故障排除

### 1. 端口冲突
如果80端口或40000端口被占用，可以修改 `docker-compose.yml` 中的端口映射：

```yaml
ports:
  - "8080:80"      # 前端改为8080端口
  - "40001:40000"  # 后端改为40001端口
```

### 2. 数据库连接失败
检查数据库服务是否正常启动：
```bash
docker-compose logs mysql
```

### 3. 前端无法访问后端API
检查nginx配置中的代理设置，确保后端服务名称正确。

### 4. 内存不足
如果遇到内存不足问题，可以调整JVM参数：
```yaml
environment:
  JAVA_OPTS: "-Xms256m -Xmx512m"
```

## 生产环境部署

### 1. 修改配置
- 更新数据库密码
- 配置域名和SSL证书
- 调整JVM参数
- 配置日志轮转

### 2. 安全配置
- 修改默认密码
- 配置防火墙
- 启用HTTPS
- 设置访问控制

### 3. 监控配置
- 配置日志监控
- 设置健康检查
- 配置告警机制

## 备份和恢复

### 备份数据库
```bash
docker-compose exec mysql mysqldump -u root -p love_diary > backup.sql
```

### 恢复数据库
```bash
docker-compose exec -T mysql mysql -u root -p love_diary < backup.sql
```

## 更新部署

### 1. 停止服务
```bash
docker-compose down
```

### 2. 拉取最新代码
```bash
git pull
```

### 3. 重新构建和启动
```bash
docker-compose up -d --build
```

## 技术支持

如果遇到问题，请检查：
1. Docker和Docker Compose版本
2. 系统资源使用情况
3. 网络连接状态
4. 服务日志信息
