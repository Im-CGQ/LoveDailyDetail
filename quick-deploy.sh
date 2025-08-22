#!/bin/bash

# LoveDaily项目快速部署脚本
# 使用方法: ./quick-deploy.sh [GitHub用户名] [仓库名]

set -e

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${GREEN}🚀 LoveDaily项目快速部署${NC}"

# 获取参数
GITHUB_USER=${1:-"yourusername"}
REPO_NAME=${2:-"LoveDailyDetail"}

echo "GitHub用户: $GITHUB_USER"
echo "仓库名: $REPO_NAME"

# 检查Git
if ! command -v git &> /dev/null; then
    echo -e "${RED}❌ Git未安装，请先安装Git${NC}"
    exit 1
fi

# 检查Docker
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker未安装，请先安装Docker${NC}"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}❌ Docker Compose未安装，请先安装Docker Compose${NC}"
    exit 1
fi

# 克隆或更新项目
if [ ! -d "$REPO_NAME" ]; then
    echo "📥 克隆项目..."
    git clone git@github.com:$GITHUB_USER/$REPO_NAME.git
else
    echo "🔄 更新项目..."
    cd "$REPO_NAME"
    git pull origin main
    cd ..
fi

# 进入项目目录
cd "$REPO_NAME"

# 检查必需文件
echo "📋 检查项目文件..."
if [ ! -f "frontend/package.json" ] || [ ! -f "backend/pom.xml" ] || [ ! -f "docker/docker-compose.yml" ]; then
    echo -e "${RED}❌ 项目文件不完整${NC}"
    exit 1
fi

# 运行部署
echo "🚀 开始部署..."
if [ -f "deploy.sh" ]; then
    chmod +x deploy.sh
    ./deploy.sh
else
    echo "🔧 手动部署..."
    cd docker
    mkdir -p logs/backend logs/nginx
    docker-compose up -d --build
    cd ..
fi

echo -e "${GREEN}✅ 部署完成！${NC}"
echo ""
echo "📱 访问地址："
echo "   前端: http://localhost"
echo "   后端: http://localhost:40000/api"
echo ""
echo "🔧 管理命令："
echo "   cd $REPO_NAME/docker && docker-compose ps"

