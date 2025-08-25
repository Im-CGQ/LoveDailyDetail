#!/bin/bash

# LoveDaily 一键部署脚本
# 使用方法: ./deploy.sh

set -e

echo "🚀 开始部署 LoveDaily 项目..."

# 检查Docker是否安装
if ! command -v docker &> /dev/null; then
    echo "❌ Docker 未安装，请先安装 Docker"
    exit 1
fi

# 检查Docker Compose是否安装
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose 未安装，请先安装 Docker Compose"
    exit 1
fi

# 检查端口占用
echo "🔍 检查端口占用..."
if netstat -tulpn 2>/dev/null | grep -q ":80 "; then
    echo "⚠️  端口80已被占用，请检查"
fi

if netstat -tulpn 2>/dev/null | grep -q ":9999 "; then
    echo "⚠️  端口9999已被占用，请检查"
fi


# 创建日志目录
echo "📁 创建日志目录..."
mkdir -p logs/backend logs/nginx

# 停止现有服务
echo "🛑 停止现有服务..."
docker-compose down 2>/dev/null || true

# 清理旧镜像（可选）
read -p "是否清理旧的Docker镜像？(y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "🧹 清理旧镜像..."
    docker system prune -f
fi

# 预拉取依赖镜像（优先国内镜像，避免 Docker Hub 超时）
echo "🐳 预拉取依赖镜像..."
set +e
docker pull mysql:8.0.35
if [ $? -ne 0 ]; then
    echo "⚠️  直接从 Docker Hub 拉取失败，尝试使用国内镜像 m.daocloud.io..."
    docker pull m.daocloud.io/library/mysql:8.0.35
    if [ $? -eq 0 ]; then
        docker tag m.daocloud.io/library/mysql:8.0.35 mysql:8.0.35
        echo "✅ 已通过国内镜像获取 mysql:8.0.35"
    else
        echo "❌ 国内镜像拉取失败，请检查服务器网络/代理设置"
    fi
fi
set -e

# 构建和启动服务
echo "🔨 构建和启动服务..."
docker-compose up -d --build

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 30

# 检查服务状态
echo "📊 检查服务状态..."
docker-compose ps

if curl -s http://localhost > /dev/null 2>&1; then
    echo "✅ 前端服务访问正常"
else
    echo "⚠️  前端服务可能还在启动中"
fi

# 显示访问信息
echo ""
echo "🎉 部署完成！"
echo ""
echo "📱 访问地址："
echo "   前端应用: http://localhost"
echo "   后端API:  http://localhost:9999/api"
echo "   数据库:   localhost:3306"
echo ""
echo "🔧 管理命令："
echo "   查看状态: docker-compose ps"
echo "   查看日志: docker-compose logs -f"
echo "   停止服务: docker-compose down"
echo "   重启服务: docker-compose restart"
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
    echo "📋 显示服务日志（按 Ctrl+C 退出）..."
    docker-compose logs -f
fi
