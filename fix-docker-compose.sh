#!/bin/bash

# Docker Compose 问题修复脚本
# 解决 Docker Compose v2 的兼容性问题

set -e

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}🔧 Docker Compose 问题修复脚本${NC}"
echo "=================================="

# 检查 Docker 版本
echo -e "${BLUE}🐳 检查 Docker 版本...${NC}"
DOCKER_VERSION=$(docker --version)
echo "Docker: $DOCKER_VERSION"

# 检查 Docker Compose 版本
echo -e "${BLUE}📦 检查 Docker Compose 版本...${NC}"
if command -v docker-compose &> /dev/null; then
    DOCKER_COMPOSE_VERSION=$(docker-compose --version)
    echo "Docker Compose: $DOCKER_COMPOSE_VERSION"
else
    echo -e "${RED}❌ Docker Compose 未安装${NC}"
    exit 1
fi

# 检查 docker compose 插件
echo -e "${BLUE}🔍 检查 Docker Compose 插件...${NC}"
if docker compose version &> /dev/null; then
    echo -e "${GREEN}✅ Docker Compose 插件可用${NC}"
    USE_PLUGIN=true
else
    echo -e "${YELLOW}⚠️  Docker Compose 插件不可用，使用独立版本${NC}"
    USE_PLUGIN=false
fi

# 修复 docker-compose.yml 文件
echo -e "${BLUE}🔧 修复 docker-compose.yml 文件...${NC}"

# 备份原文件
cp docker-compose.yml docker-compose.yml.backup
echo "已备份原文件为 docker-compose.yml.backup"

# 创建修复后的文件
cat > docker-compose.yml << 'EOF'
services:
  # MySQL数据库
  mysql:
    image: mysql:8.0
    container_name: lovediary_mysql
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: 123456
      MYSQL_DATABASE: love_diary
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./database/init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - lovediary_network
    command: --default-authentication-plugin=mysql_native_password

  # 后端服务
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: lovediary_backend
    restart: unless-stopped
    ports:
      - "40000:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/love_diary?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: 123456
      SERVER_PORT: 8080
      SERVER_ADDRESS: 0.0.0.0
      SERVER_SERVLET_CONTEXT_PATH: /api
    depends_on:
      mysql:
        condition: service_healthy
    networks:
      - lovediary_network
    volumes:
      - ./logs/backend:/app/logs

  # 前端服务
  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: lovediary_frontend
    restart: unless-stopped
    ports:
      - "80:80"
    depends_on:
      - backend
    networks:
      - lovediary_network
    volumes:
      - ./logs/nginx:/var/log/nginx

volumes:
  mysql_data:
    driver: local

networks:
  lovediary_network:
    driver: bridge
EOF

echo -e "${GREEN}✅ docker-compose.yml 文件已修复${NC}"

# 创建必要的目录
echo -e "${BLUE}📁 创建必要的目录...${NC}"
mkdir -p logs/backend logs/nginx
echo -e "${GREEN}✅ 目录创建完成${NC}"

# 测试配置文件
echo -e "${BLUE}🧪 测试配置文件...${NC}"
if [ "$USE_PLUGIN" = true ]; then
    if docker compose config > /dev/null 2>&1; then
        echo -e "${GREEN}✅ 配置文件语法正确（插件版本）${NC}"
    else
        echo -e "${RED}❌ 配置文件语法错误（插件版本）${NC}"
    fi
fi

if docker-compose config > /dev/null 2>&1; then
    echo -e "${GREEN}✅ 配置文件语法正确（独立版本）${NC}"
else
    echo -e "${RED}❌ 配置文件语法错误（独立版本）${NC}"
    echo "错误详情："
    docker-compose config
    exit 1
fi

# 清理旧的容器和镜像（可选）
echo -e "${BLUE}🧹 清理旧的容器和镜像...${NC}"
read -p "是否要清理旧的容器和镜像？(y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "停止并删除旧容器..."
    docker-compose down --remove-orphans 2>/dev/null || true
    
    echo "删除未使用的镜像..."
    docker image prune -f
    
    echo -e "${GREEN}✅ 清理完成${NC}"
fi

echo ""
echo -e "${GREEN}🎉 修复完成！${NC}"
echo ""
echo -e "${BLUE}🔧 现在可以使用以下命令启动服务：${NC}"
if [ "$USE_PLUGIN" = true ]; then
    echo "docker compose up -d"
else
    echo "docker-compose up -d"
fi
echo ""
echo -e "${BLUE}📋 其他常用命令：${NC}"
if [ "$USE_PLUGIN" = true ]; then
    echo "查看状态: docker compose ps"
    echo "查看日志: docker compose logs -f"
    echo "重启服务: docker compose restart"
    echo "停止服务: docker compose down"
else
    echo "查看状态: docker-compose ps"
    echo "查看日志: docker-compose logs -f"
    echo "重启服务: docker-compose restart"
    echo "停止服务: docker-compose down"
fi
