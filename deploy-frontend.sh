#!/bin/bash

# 设置错误时退出
set -e

echo "开始部署前端服务..."

# Git操作：暂存当前修改并拉取最新代码
echo "Git操作：暂存当前修改..."
git stash

echo "Git操作：拉取最新代码..."
git pull

# 复制配置文件
echo "复制配置文件..."
cp ../backend/application.yml ./backend/src/main/resources/

# 进入frontend目录
echo "进入frontend目录..."
cd ./frontend

# 安装依赖
echo "安装npm依赖..."
npm i

# 构建项目
echo "构建前端项目..."
npm run build

# 停止并删除旧容器
echo "停止旧容器..."
docker stop frontend || true
echo "删除旧容器..."
docker rm frontend || true

# 删除旧镜像
echo "删除旧镜像..."
docker rmi my-frontend-app || true

# 构建Docker镜像
echo "构建Docker镜像..."
docker build -t my-frontend-app .

# 运行新容器
echo "启动新容器..."
docker run -d --name frontend -p 80:80 my-frontend-app

echo "前端服务部署完成！"
echo "容器名称: frontend"
echo "端口映射: 80:80"
echo "镜像名称: my-frontend-app"
