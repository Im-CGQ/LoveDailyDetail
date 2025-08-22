#!/bin/bash

# LoveDaily项目自动部署脚本
# 使用方法: ./auto-deploy.sh

set -e

echo "🚀 开始部署LoveDaily项目..."

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 1. 检查Git是否安装
log_info "检查Git安装状态..."
if ! command -v git &> /dev/null; then
    log_warning "Git未安装，正在安装..."
    
    # 检测系统类型
    if command -v apt &> /dev/null; then
        # Ubuntu/Debian
        sudo apt update
        sudo apt install git -y
    elif command -v yum &> /dev/null; then
        # CentOS 7
        sudo yum install git -y
    elif command -v dnf &> /dev/null; then
        # CentOS 8/RHEL 8
        sudo dnf install git -y
    else
        log_error "无法识别系统类型，请手动安装Git"
        exit 1
    fi
    
    log_success "Git安装完成"
else
    log_success "Git已安装: $(git --version)"
fi

# 2. 配置Git用户信息
log_info "配置Git用户信息..."
if [ -z "$(git config --global user.name)" ]; then
    log_warning "Git用户信息未配置"
    read -p "请输入GitHub用户名: " github_username
    read -p "请输入GitHub邮箱: " github_email
    
    if [ -n "$github_username" ] && [ -n "$github_email" ]; then
        git config --global user.name "$github_username"
        git config --global user.email "$github_email"
        log_success "Git用户信息配置完成"
    else
        log_error "用户名或邮箱不能为空"
        exit 1
    fi
else
    log_success "Git用户信息已配置: $(git config --global user.name) <$(git config --global user.email)>"
fi

# 3. 检查SSH密钥
log_info "检查SSH密钥..."
if [ ! -f ~/.ssh/id_rsa ]; then
    log_warning "SSH密钥不存在，正在生成..."
    
    # 启动ssh-agent
    eval "$(ssh-agent -s)" > /dev/null 2>&1
    
    # 生成SSH密钥
    ssh-keygen -t rsa -b 4096 -C "$(git config --global user.email)" -f ~/.ssh/id_rsa -N "" > /dev/null 2>&1
    
    # 添加密钥到ssh-agent
    ssh-add ~/.ssh/id_rsa > /dev/null 2>&1
    
    log_success "SSH密钥生成完成"
    echo ""
    log_warning "请将以下公钥添加到GitHub:"
    echo "1. 复制下面的公钥内容"
    echo "2. 登录GitHub，进入Settings -> SSH and GPG keys -> New SSH key"
    echo "3. 粘贴公钥内容并保存"
    echo ""
    echo "公钥内容:"
    cat ~/.ssh/id_rsa.pub
    echo ""
    read -p "添加完成后按回车键继续..."
else
    log_success "SSH密钥已存在"
fi

# 4. 测试GitHub SSH连接
log_info "测试GitHub SSH连接..."
if ssh -T git@github.com 2>&1 | grep -q "successfully authenticated"; then
    log_success "GitHub SSH连接测试成功"
else
    log_warning "GitHub SSH连接测试失败，请检查SSH密钥配置"
    read -p "是否继续？(y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# 5. 获取项目信息
log_info "获取项目信息..."
read -p "请输入GitHub用户名: " repo_username
read -p "请输入仓库名: " repo_name

if [ -z "$repo_username" ] || [ -z "$repo_name" ]; then
    log_error "用户名或仓库名不能为空"
    exit 1
fi

# 6. 克隆或更新项目
if [ ! -d "$repo_name" ]; then
    log_info "克隆项目: $repo_username/$repo_name"
    git clone git@github.com:$repo_username/$repo_name.git
    log_success "项目克隆完成"
else
    log_info "项目已存在，更新代码..."
    cd "$repo_name"
    git pull origin main
    log_success "代码更新完成"
    cd ..
fi

# 7. 进入项目目录
cd "$repo_name"

# 8. 检查项目文件完整性
log_info "检查项目文件完整性..."
required_files=(
    "frontend/package.json"
    "backend/pom.xml"
    "docker/docker-compose.yml"
    "database/init.sql"
)

missing_files=()
for file in "${required_files[@]}"; do
    if [ ! -f "$file" ]; then
        missing_files+=("$file")
    fi
done

if [ ${#missing_files[@]} -gt 0 ]; then
    log_error "以下必需文件缺失:"
    for file in "${missing_files[@]}"; do
        echo "  - $file"
    done
    exit 1
fi

log_success "项目文件完整性检查通过"

# 9. 检查Docker环境
log_info "检查Docker环境..."
if ! command -v docker &> /dev/null; then
    log_error "Docker未安装，请先安装Docker"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    log_error "Docker Compose未安装，请先安装Docker Compose"
    exit 1
fi

log_success "Docker环境检查通过"

# 10. 检查端口占用
log_info "检查端口占用..."
if netstat -tulpn 2>/dev/null | grep -q ":80 "; then
    log_warning "端口80已被占用"
fi

if netstat -tulpn 2>/dev/null | grep -q ":40000 "; then
    log_warning "端口40000已被占用"
fi

# 11. 运行部署脚本
log_info "开始部署项目..."
if [ -f "deploy.sh" ]; then
    chmod +x deploy.sh
    ./deploy.sh
else
    log_warning "deploy.sh不存在，手动执行部署步骤..."
    
    # 进入docker目录
    cd docker
    
    # 创建日志目录
    mkdir -p logs/backend logs/nginx
    
    # 启动服务
    docker-compose up -d --build
    
    # 等待服务启动
    sleep 30
    
    # 检查服务状态
    docker-compose ps
    
    cd ..
fi

# 12. 显示访问信息
echo ""
log_success "🎉 部署完成！"
echo ""
echo "📱 访问地址："
echo "   前端应用: http://localhost"
echo "   后端API:  http://localhost:40000/api"
echo "   数据库:   localhost:3306"
echo ""
echo "🔧 管理命令："
echo "   查看状态: cd $repo_name/docker && docker-compose ps"
echo "   查看日志: cd $repo_name/docker && docker-compose logs -f"
echo "   停止服务: cd $repo_name/docker && docker-compose down"
echo "   重启服务: cd $repo_name/docker && docker-compose restart"
echo ""
echo "📋 默认配置："
echo "   数据库名: love_diary"
echo "   用户名:   root"
echo "   密码:     123456"
echo ""

# 13. 询问是否查看日志
read -p "是否查看服务日志？(y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    log_info "显示服务日志（按 Ctrl+C 退出）..."
    cd docker
    docker-compose logs -f
fi

