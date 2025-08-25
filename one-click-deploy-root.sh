#!/bin/bash

# LoveDaily项目一键部署脚本（允许root用户）
# 使用方法: ./one-click-deploy-root.sh [GitHub用户名] [仓库名]

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
GITHUB_USER=${1:-"Im-CGQ"}
REPO_NAME=${2:-"LoveDailyDetail"}

echo "GitHub用户: $GITHUB_USER"
echo "仓库名: $REPO_NAME"
echo ""

# 检查端口占用
echo -e "${BLUE}🔍 检查端口占用...${NC}"
if netstat -tulpn 2>/dev/null | grep -q ":80 "; then
    echo -e "${YELLOW}⚠️  端口80已被占用${NC}"
fi
if netstat -tulpn 2>/dev/null | grep -q ":9999 "; then
    echo -e "${YELLOW}⚠️  端口9999已被占用${NC}"
fi

# 安装Git
echo -e "${BLUE}📦 检查Git...${NC}"
if ! command -v git &> /dev/null; then
    echo "安装Git..."
    if command -v apt &> /dev/null; then
        apt update && apt install git -y
    elif command -v yum &> /dev/null; then
        yum install git -y
    elif command -v dnf &> /dev/null; then
        dnf install git -y
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
    echo "添加完成后，按任意键继续..."
    read -n 1 -s
else
    echo -e "${GREEN}✅ SSH密钥已存在${NC}"
fi

# 测试GitHub连接
echo -e "${BLUE}🔗 测试GitHub连接...${NC}"
if ssh -T git@github.com 2>&1 | grep -q "successfully authenticated"; then
    echo -e "${GREEN}✅ GitHub连接成功${NC}"
else
    echo -e "${YELLOW}⚠️  GitHub连接失败，请检查SSH密钥配置${NC}"
    echo "按任意键继续..."
    read -n 1 -s
fi

# 安装Docker
echo -e "${BLUE}🐳 检查Docker...${NC}"
if ! command -v docker &> /dev/null; then
    echo "安装Docker..."
    curl -fsSL https://get.docker.com -o get-docker.sh
    sh get-docker.sh
    rm get-docker.sh
    
    # 启动Docker服务
    systemctl start docker
    systemctl enable docker
else
    echo -e "${GREEN}✅ Docker已安装${NC}"
fi

# 安装Docker Compose
echo -e "${BLUE}📦 检查Docker Compose...${NC}"
if ! command -v docker-compose &> /dev/null; then
    echo "安装Docker Compose..."
    curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose
else
    echo -e "${GREEN}✅ Docker Compose已安装${NC}"
fi

# 克隆或更新项目
echo -e "${BLUE}📥 克隆项目...${NC}"
if [ -d "$REPO_NAME" ]; then
    echo "项目已存在，更新代码..."
    cd "$REPO_NAME"
    # 检查是否有本地修改
    if [ -n "$(git status --porcelain)" ]; then
        echo -e "${YELLOW}⚠️  检测到本地修改${NC}"
        echo "本地修改的文件:"
        git status --short
        git stash
    fi
    git pull origin main
else
    echo "克隆项目..."
    git clone git@github.com:$GITHUB_USER/$REPO_NAME.git
    cd "$REPO_NAME"
fi


# 运行部署脚本
echo -e "${BLUE}🚀 开始部署...${NC}"
chmod +x deploy.sh
./deploy.sh

echo -e "${GREEN}🎉 部署完成！${NC}"
echo ""
echo -e "${BLUE}📋 访问信息:${NC}"
echo "前端: http://localhost"
echo "后端API: http://localhost:9999/api"
echo ""
echo -e "${BLUE}🔧 管理命令:${NC}"
echo "查看状态: cd $REPO_NAME/docker && docker-compose ps"
echo "查看日志: cd $REPO_NAME/docker && docker-compose logs -f"
echo "重启服务: cd $REPO_NAME/docker && docker-compose restart"
echo "停止服务: cd $REPO_NAME/docker && docker-compose down"

