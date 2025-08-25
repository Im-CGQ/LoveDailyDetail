#!/bin/bash

# 设置错误时退出
set -e

echo "开始部署后端服务..."

# Git操作：暂存当前修改并拉取最新代码
echo "Git操作：暂存当前修改..."
git stash

echo "Git操作：拉取最新代码..."
git pull

# 复制配置文件
echo "复制配置文件..."
cp ../backend/application.yml ./backend/src/main/resources/

# 进入backend目录
echo "进入backend目录..."
cd ./backend

# 停止并删除旧容器
echo "停止旧容器..."
docker stop love-diary || true
echo "删除旧容器..."
docker rm  love-diary || true

# 清理并打包项目
echo "清理并打包项目..."
mvn clean package -DskipTests

# 构建Docker镜像
echo "构建Docker镜像..."
docker build -t love-diary-backend:1.0.0 .

# 运行新容器
echo "启动新容器..."
docker run -d -p 9999:9999 --name love-diary love-diary-backend:1.0.0

echo "后端服务部署完成！"
echo "容器名称: love-diary"
echo "端口映射: 9999:9999"
echo "镜像版本: love-diary-backend:1.0.0"
