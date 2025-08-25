#!/bin/bash

# 设置错误时退出
set -e

echo "开始拉取最新代码..."

# Git操作：暂存当前修改并拉取最新代码
echo "Git操作：暂存当前修改..."
git stash

echo "Git操作：拉取最新代码..."
git pull

# 复制配置文件
echo "复制配置文件..."
cp ../backend/application.yml ./backend/src/main/resources/

echo "代码拉取完成！"

