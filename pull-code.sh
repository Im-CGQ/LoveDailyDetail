#!/bin/bash

# LoveDaily项目代码拉取脚本
# 使用方法: ./pull-code.sh [GitHub用户名] [仓库名] [分支名]

set -e

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}📥 LoveDaily项目代码拉取脚本${NC}"
echo "=================================="

# 获取参数
GITHUB_USER=${1:-"Im-CGQ"}
REPO_NAME=${2:-"LoveDailyDetail"}
BRANCH=${3:-"main"}

echo "GitHub用户: $GITHUB_USER"
echo "仓库名: $REPO_NAME"
echo "分支名: $BRANCH"
echo ""

# 检查Git是否安装
echo -e "${BLUE}📦 检查Git...${NC}"
if ! command -v git &> /dev/null; then
    echo -e "${RED}❌ Git未安装，请先安装Git${NC}"
    exit 1
else
    echo -e "${GREEN}✅ Git已安装${NC}"
fi

# 配置Git（如果需要）
echo -e "${BLUE}⚙️  检查Git配置...${NC}"
if [ -z "$(git config --global user.name)" ]; then
    echo "配置Git用户信息..."
    read -p "请输入GitHub用户名: " github_username
    read -p "请输入GitHub邮箱: " github_email
    git config --global user.name "$github_username"
    git config --global user.email "$github_email"
    echo -e "${GREEN}✅ Git配置完成${NC}"
else
    echo -e "${GREEN}✅ Git已配置${NC}"
fi

# 检查SSH密钥
echo -e "${BLUE}🔑 检查SSH密钥...${NC}"
if [ ! -f ~/.ssh/id_rsa ]; then
    echo -e "${YELLOW}⚠️  未找到SSH密钥，将使用HTTPS方式克隆${NC}"
    USE_HTTPS=true
else
    echo -e "${GREEN}✅ SSH密钥已存在${NC}"
    USE_HTTPS=false
    
    # 测试GitHub连接
    echo -e "${BLUE}🔗 测试GitHub SSH连接...${NC}"
    if ssh -T git@github.com 2>&1 | grep -q "successfully authenticated"; then
        echo -e "${GREEN}✅ GitHub SSH连接成功${NC}"
    else
        echo -e "${YELLOW}⚠️  GitHub SSH连接失败，将使用HTTPS方式${NC}"
        USE_HTTPS=true
    fi
fi

# 克隆或更新项目
echo -e "${BLUE}📥 处理项目代码...${NC}"
if [ -d "$REPO_NAME" ]; then
    echo "项目已存在，更新代码..."
    cd "$REPO_NAME"
    
    # 保存当前分支
    CURRENT_BRANCH=$(git branch --show-current)
    echo "当前分支: $CURRENT_BRANCH"
    
    # 获取远程更新
    echo "获取远程更新..."
    git fetch origin
    
    # 检查是否有本地修改
    if [ -n "$(git status --porcelain)" ]; then
        echo -e "${YELLOW}⚠️  检测到本地修改${NC}"
        echo "本地修改的文件:"
        git status --short
        
        read -p "是否要暂存本地修改？(y/n): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            echo "暂存本地修改..."
            git stash push -m "Auto stash before pull - $(date)"
            STASHED=true
        else
            echo -e "${RED}❌ 请先处理本地修改${NC}"
            exit 1
        fi
    fi
    
    # 切换到目标分支
    if [ "$CURRENT_BRANCH" != "$BRANCH" ]; then
        echo "切换到分支: $BRANCH"
        if git show-ref --verify --quiet refs/remotes/origin/$BRANCH; then
            git checkout -B $BRANCH origin/$BRANCH
        else
            echo -e "${RED}❌ 分支 $BRANCH 不存在${NC}"
            exit 1
        fi
    fi
    
    # 拉取最新代码
    echo "拉取最新代码..."
    git pull origin $BRANCH
    
    # 恢复暂存的修改
    if [ "$STASHED" = true ]; then
        echo "恢复暂存的修改..."
        if git stash list | grep -q "Auto stash before pull"; then
            git stash pop
            echo -e "${GREEN}✅ 本地修改已恢复${NC}"
        fi
    fi
    
else
    echo "克隆项目..."
    if [ "$USE_HTTPS" = true ]; then
        git clone "https://github.com/$GITHUB_USER/$REPO_NAME.git"
    else
        git clone "git@github.com:$GITHUB_USER/$REPO_NAME.git"
    fi
    cd "$REPO_NAME"
    
    # 切换到指定分支
    if [ "$BRANCH" != "main" ]; then
        echo "切换到分支: $BRANCH"
        if git show-ref --verify --quiet refs/remotes/origin/$BRANCH; then
            git checkout $BRANCH
        else
            echo -e "${YELLOW}⚠️  分支 $BRANCH 不存在，使用main分支${NC}"
        fi
    fi
fi

# 显示最新提交信息
echo -e "${BLUE}📋 最新提交信息:${NC}"
git log --oneline -5

# 检查项目文件完整性
echo -e "${BLUE}🔍 检查项目文件完整性...${NC}"
required_files=("docker-compose.yml" "backend/Dockerfile" "frontend/Dockerfile" "database/init.sql")
missing_files=()

for file in "${required_files[@]}"; do
    if [ ! -f "$file" ]; then
        missing_files+=("$file")
    fi
done

if [ ${#missing_files[@]} -eq 0 ]; then
    echo -e "${GREEN}✅ 项目文件完整${NC}"
else
    echo -e "${YELLOW}⚠️  缺少以下文件:${NC}"
    for file in "${missing_files[@]}"; do
        echo "  - $file"
    done
fi

# 显示项目状态
echo -e "${BLUE}📊 项目状态:${NC}"
echo "项目路径: $(pwd)"
echo "当前分支: $(git branch --show-current)"
echo "最新提交: $(git log -1 --pretty=format:'%h - %s (%cr)')"
echo "远程仓库: $(git remote get-url origin)"

echo ""
echo -e "${GREEN}🎉 代码拉取完成！${NC}"
echo ""
echo -e "${BLUE}🔧 常用命令:${NC}"
echo "查看状态: git status"
echo "查看日志: git log --oneline -10"
echo "查看分支: git branch -a"
echo "切换分支: git checkout <分支名>"
echo "查看远程: git remote -v"
echo ""
echo -e "${BLUE}📁 项目结构:${NC}"
echo "├── backend/          # 后端代码"
echo "├── frontend/         # 前端代码"
echo "├── database/         # 数据库脚本"
echo "├── docker-compose.yml # Docker配置"
echo "└── deploy.sh         # 部署脚本"
