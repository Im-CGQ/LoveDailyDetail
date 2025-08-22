#!/bin/bash

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🔧 修复Docker注册表超时问题...${NC}"

# 检查Docker是否运行
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}❌ Docker未运行，请先启动Docker${NC}"
    exit 1
fi

echo -e "${BLUE}📋 尝试解决方案...${NC}"

# 方案1: 重启Docker服务
echo -e "${YELLOW}1️⃣ 重启Docker服务...${NC}"
if command -v systemctl > /dev/null 2>&1; then
    sudo systemctl restart docker
elif command -v service > /dev/null 2>&1; then
    sudo service docker restart
else
    echo -e "${YELLOW}⚠️  请手动重启Docker Desktop${NC}"
fi

# 等待Docker启动
echo -e "${BLUE}⏳ 等待Docker启动...${NC}"
sleep 5

# 方案2: 清理Docker缓存
echo -e "${YELLOW}2️⃣ 清理Docker缓存...${NC}"
docker system prune -f

# 方案3: 测试网络连接
echo -e "${YELLOW}3️⃣ 测试Docker Hub连接...${NC}"
if curl -s --connect-timeout 10 https://registry-1.docker.io/v2/ > /dev/null; then
    echo -e "${GREEN}✅ Docker Hub连接正常${NC}"
else
    echo -e "${RED}❌ Docker Hub连接失败，尝试使用镜像...${NC}"
    
    # 创建daemon.json配置
    DOCKER_CONFIG_DIR="/etc/docker"
    if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" ]]; then
        # Windows
        DOCKER_CONFIG_DIR="$HOME/.docker"
    fi
    
    if [[ ! -d "$DOCKER_CONFIG_DIR" ]]; then
        sudo mkdir -p "$DOCKER_CONFIG_DIR"
    fi
    
    # 备份现有配置
    if [[ -f "$DOCKER_CONFIG_DIR/daemon.json" ]]; then
        sudo cp "$DOCKER_CONFIG_DIR/daemon.json" "$DOCKER_CONFIG_DIR/daemon.json.backup"
    fi
    
    # 创建新的daemon.json
    sudo tee "$DOCKER_CONFIG_DIR/daemon.json" > /dev/null <<EOF
{
  "registry-mirrors": [
    "https://docker.mirrors.ustc.edu.cn",
    "https://hub-mirror.c.163.com",
    "https://mirror.baidubce.com"
  ],
  "insecure-registries": [],
  "debug": false,
  "experimental": false
}
EOF
    
    echo -e "${GREEN}✅ Docker镜像配置已更新，请重启Docker服务${NC}"
fi

# 方案4: 尝试拉取镜像
echo -e "${YELLOW}4️⃣ 尝试拉取MySQL镜像...${NC}"
if docker pull mysql:8.0.35; then
    echo -e "${GREEN}✅ MySQL镜像拉取成功${NC}"
else
    echo -e "${RED}❌ MySQL镜像拉取失败${NC}"
    echo -e "${YELLOW}💡 建议尝试以下操作:${NC}"
    echo "  1. 检查网络连接"
    echo "  2. 使用VPN或代理"
    echo "  3. 重启Docker服务"
    echo "  4. 使用本地MySQL替代Docker MySQL"
fi

echo -e "${GREEN}🎉 修复完成！${NC}"
echo -e "${YELLOW}💡 如果问题仍然存在，请尝试手动重启Docker Desktop${NC}"
