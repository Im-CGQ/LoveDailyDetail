#!/bin/bash

# Docker和Docker Compose安装脚本
# 使用方法: ./install-docker.sh

set -e

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}🐳 Docker和Docker Compose安装脚本${NC}"

# 检测系统类型
if [ -f /etc/os-release ]; then
    . /etc/os-release
    OS=$NAME
    VER=$VERSION_ID
else
    echo -e "${RED}无法检测系统类型${NC}"
    exit 1
fi

echo "检测到系统: $OS $VER"

# 安装Docker
install_docker() {
    echo -e "${BLUE}📦 安装Docker...${NC}"
    
    if command -v apt &> /dev/null; then
        # Ubuntu/Debian
        echo "使用apt安装Docker..."
        sudo apt update
        sudo apt install -y apt-transport-https ca-certificates curl gnupg lsb-release
        
        # 添加Docker官方GPG密钥
        curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
        
        # 添加Docker仓库
        echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
        
        sudo apt update
        sudo apt install -y docker-ce docker-ce-cli containerd.io
        
    elif command -v yum &> /dev/null; then
        # CentOS/RHEL
        echo "使用yum安装Docker..."
        sudo yum install -y yum-utils
        sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
        sudo yum install -y docker-ce docker-ce-cli containerd.io
        
    elif command -v dnf &> /dev/null; then
        # CentOS 8/RHEL 8
        echo "使用dnf安装Docker..."
        sudo dnf install -y dnf-utils
        sudo dnf config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
        sudo dnf install -y docker-ce docker-ce-cli containerd.io
        
    else
        echo -e "${RED}不支持的系统类型${NC}"
        exit 1
    fi
    
    # 启动Docker服务
    sudo systemctl start docker
    sudo systemctl enable docker
    
    # 添加当前用户到docker组
    sudo usermod -aG docker $USER
    
    echo -e "${GREEN}✅ Docker安装完成${NC}"
}

# 安装Docker Compose
install_docker_compose() {
    echo -e "${BLUE}📦 安装Docker Compose...${NC}"
    
    # 下载Docker Compose
    sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    
    # 设置执行权限
    sudo chmod +x /usr/local/bin/docker-compose
    
    # 创建软链接
    sudo ln -sf /usr/local/bin/docker-compose /usr/bin/docker-compose
    
    echo -e "${GREEN}✅ Docker Compose安装完成${NC}"
}

# 检查是否已安装
if command -v docker &> /dev/null; then
    echo -e "${YELLOW}⚠️  Docker已安装: $(docker --version)${NC}"
else
    install_docker
fi

if command -v docker-compose &> /dev/null; then
    echo -e "${YELLOW}⚠️  Docker Compose已安装: $(docker-compose --version)${NC}"
else
    install_docker_compose
fi

# 验证安装
echo -e "${BLUE}🔍 验证安装...${NC}"
docker --version
docker-compose --version

# 测试Docker
echo -e "${BLUE}🧪 测试Docker...${NC}"
sudo docker run hello-world

echo -e "${GREEN}🎉 Docker环境安装完成！${NC}"
echo ""
echo -e "${YELLOW}注意: 如果当前用户不在docker组中，请重新登录或运行以下命令:${NC}"
echo "sudo usermod -aG docker $USER"
echo "newgrp docker"
echo ""
echo -e "${BLUE}现在可以运行部署脚本了:${NC}"
echo "./auto-deploy.sh"

