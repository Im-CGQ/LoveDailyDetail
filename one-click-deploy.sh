#!/bin/bash

# LoveDaily项目一键部署脚本
# 使用方法: ./one-click-deploy.sh [GitHub用户名] [仓库名]

set -e

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}🚀 LoveDaily项目一键部署脚本${NC}"
echo "=================================="

# 获取参数
GITHUB_USER=${1:-"yourusername"}
REPO_NAME=${2:-"LoveDailyDetail"}

echo "GitHub用户: $GITHUB_USER"
echo "仓库名: $REPO_NAME"
echo ""

# 检查是否为root用户
if [ "$EUID" -eq 0 ]; then
    echo -e "${RED}❌ 请不要使用root用户运行此脚本${NC}"
    exit 1
fi

# 检查系统要求
echo -e "${BLUE}📋 检查系统要求...${NC}"

# 检查内存
MEMORY_KB=$(grep MemTotal /proc/meminfo | awk '{print $2}')
MEMORY_GB=$((MEMORY_KB / 1024 / 1024))
if [ $MEMORY_GB -lt 2 ]; then
    echo -e "${YELLOW}⚠️  内存不足，建议至少2GB内存${NC}"
fi
echo "内存: ${MEMORY_GB}GB"

# 检查磁盘空间
DISK_SPACE=$(df / | awk 'NR==2 {print $4}')
DISK_SPACE_GB=$((DISK_SPACE / 1024 / 1024))
if [ $DISK_SPACE_GB -lt 5 ]; then
    echo -e "${YELLOW}⚠️  磁盘空间不足，建议至少5GB可用空间${NC}"
fi
echo "可用磁盘空间: ${DISK_SPACE_GB}GB"

# 检查端口占用
echo -e "${BLUE}🔍 检查端口占用...${NC}"
if netstat -tulpn 2>/dev/null | grep -q ":80 "; then
    echo -e "${YELLOW}⚠️  端口80已被占用${NC}"
fi
if netstat -tulpn 2>/dev/null | grep -q ":40000 "; then
    echo -e "${YELLOW}⚠️  端口40000已被占用${NC}"
fi

# 安装Git
echo -e "${BLUE}📦 检查Git...${NC}"
if ! command -v git &> /dev/null; then
    echo "安装Git..."
    if command -v apt &> /dev/null; then
        sudo apt update && sudo apt install git -y
    elif command -v yum &> /dev/null; then
        sudo yum install git -y
    elif command -v dnf &> /dev/null; then
        sudo dnf install git -y
    else
        echo -e "${RED}❌ 无法安装Git${NC}"
        exit 1
    fi
else
    echo -e "${GREEN}✅ Git已安装${NC}"
fi

# 配置Git
echo -e "${BLUE}⚙️  配置Git...${NC}"
if [ -z "$(git config --global user.name)" ]; then
    echo "配置Git用户信息..."
    read -p "请输入GitHub用户名: " github_username
    read -p "请输入GitHub邮箱: " github_email
    git config --global user.name "$github_username"
    git config --global user.email "$github_email"
fi

# 检查SSH密钥
echo -e "${BLUE}🔑 检查SSH密钥...${NC}"
if [ ! -f ~/.ssh/id_rsa ]; then
    echo "生成SSH密钥..."
    ssh-keygen -t rsa -b 4096 -C "$(git config --global user.email)" -f ~/.ssh/id_rsa -N ""
    eval "$(ssh-agent -s)" > /dev/null 2>&1
    ssh-add ~/.ssh/id_rsa > /dev/null 2>&1
    
    echo -e "${YELLOW}请将以下公钥添加到GitHub:${NC}"
    echo "1. 复制下面的公钥内容"
    echo "2. 登录GitHub，进入Settings -> SSH and GPG keys -> New SSH key"
    echo "3. 粘贴公钥内容并保存"
    echo ""
    cat ~/.ssh/id_rsa.pub
    echo ""
    read -p "添加完成后按回车键继续..."
else
    echo -e "${GREEN}✅ SSH密钥已存在${NC}"
fi

# 安装Docker
echo -e "${BLUE}🐳 检查Docker...${NC}"
if ! command -v docker &> /dev/null; then
    echo "安装Docker..."
    if command -v apt &> /dev/null; then
        sudo apt update
        sudo apt install -y apt-transport-https ca-certificates curl gnupg lsb-release
        curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
        echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
        sudo apt update
        sudo apt install -y docker-ce docker-ce-cli containerd.io
    elif command -v yum &> /dev/null; then
        sudo yum install -y yum-utils
        sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
        sudo yum install -y docker-ce docker-ce-cli containerd.io
    fi
    
    sudo systemctl start docker
    sudo systemctl enable docker
    sudo usermod -aG docker $USER
    echo -e "${GREEN}✅ Docker安装完成${NC}"
else
    echo -e "${GREEN}✅ Docker已安装${NC}"
fi

# 安装Docker Compose
echo -e "${BLUE}📦 检查Docker Compose...${NC}"
if ! command -v docker-compose &> /dev/null; then
    echo "安装Docker Compose..."
    sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    sudo ln -sf /usr/local/bin/docker-compose /usr/bin/docker-compose
    echo -e "${GREEN}✅ Docker Compose安装完成${NC}"
else
    echo -e "${GREEN}✅ Docker Compose已安装${NC}"
fi

# 测试GitHub连接
echo -e "${BLUE}🔗 测试GitHub连接...${NC}"
if ssh -T git@github.com 2>&1 | grep -q "successfully authenticated"; then
    echo -e "${GREEN}✅ GitHub SSH连接成功${NC}"
else
    echo -e "${YELLOW}⚠️  GitHub SSH连接失败，请检查SSH密钥配置${NC}"
    read -p "是否继续？(y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# 克隆或更新项目
echo -e "${BLUE}📥 获取项目代码...${NC}"
if [ ! -d "$REPO_NAME" ]; then
    echo "克隆项目..."
    git clone git@github.com:Im-CGQ/LoveDailyDetail.git
else
    echo "更新项目..."
    cd "$REPO_NAME"
    git pull origin main
    cd ..
fi

# 进入项目目录
cd "$REPO_NAME"

# 检查项目文件
echo -e "${BLUE}📋 检查项目文件...${NC}"
required_files=("frontend/package.json" "backend/pom.xml" "docker/docker-compose.yml" "database/init.sql")
for file in "${required_files[@]}"; do
    if [ ! -f "$file" ]; then
        echo -e "${RED}❌ 缺少必需文件: $file${NC}"
        exit 1
    fi
done
echo -e "${GREEN}✅ 项目文件完整${NC}"

# 部署项目
echo -e "${BLUE}🚀 开始部署...${NC}"
if [ -f "deploy.sh" ]; then
    chmod +x deploy.sh
    ./deploy.sh
else
    echo "手动部署..."
    cd docker
    mkdir -p logs/backend logs/nginx
    docker-compose up -d --build
    
    # 等待服务启动
    echo "等待服务启动..."
    sleep 30
    
    # 检查服务状态
    docker-compose ps
    cd ..
fi

# 显示结果
echo ""
echo -e "${GREEN}🎉 部署完成！${NC}"
echo "=================================="
echo ""
echo "📱 访问地址："
echo "   前端应用: http://localhost"
echo "   后端API:  http://localhost:40000/api"
echo "   数据库:   localhost:3306"
echo ""
echo "🔧 管理命令："
echo "   查看状态: cd $REPO_NAME/docker && docker-compose ps"
echo "   查看日志: cd $REPO_NAME/docker && docker-compose logs -f"
echo "   停止服务: cd $REPO_NAME/docker && docker-compose down"
echo "   重启服务: cd $REPO_NAME/docker && docker-compose restart"
echo ""
echo "📋 默认配置："
echo "   数据库名: love_diary"
echo "   用户名:   root"
echo "   密码:     123456"
echo ""

# 询问是否查看日志
read -p "是否查看服务日志？(y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${BLUE}显示服务日志（按 Ctrl+C 退出）...${NC}"
    cd docker
    docker-compose logs -f
fi

