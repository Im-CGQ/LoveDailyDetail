#!/bin/bash

# 设置错误时退出
set -e

echo "开始提交数据库备份文件到git..."

# 检查git仓库状态
if ! git status > /dev/null 2>&1; then
    echo "错误：当前目录不是git仓库"
    exit 1
fi

echo "Git操作：拉取最新代码..."
git pull

# 检查是否有备份文件
BACKUP_DIR="./database/backups"
BACKUP_FILES=$(ls $BACKUP_DIR/backup_*.sql 2>/dev/null | wc -l)
if [ $BACKUP_FILES -eq 0 ]; then
    echo "没有找到备份文件，请先运行 backup-database.sh 创建备份"
    exit 1
fi

# 显示要提交的备份文件
echo "找到以下备份文件："
ls -lh $BACKUP_DIR/backup_*.sql

# 添加备份文件到git
echo "添加备份文件到git..."
git add $BACKUP_DIR/backup_*.sql

# 检查是否有文件被添加
if git diff --cached --quiet; then
    echo "没有新的备份文件需要提交"
    exit 0
fi

# 创建提交信息
COMMIT_DATE=$(date '+%Y-%m-%d %H:%M:%S')
COMMIT_MSG="数据库备份 - $COMMIT_DATE"

echo "提交备份文件..."
git commit -m "$COMMIT_MSG"

# 推送到远程仓库
echo "推送到远程仓库..."
if git push; then
    echo "数据库备份文件已成功推送到远程仓库！"
else
    echo "警告：推送到远程仓库失败，但本地提交已成功"
    echo "请手动执行: git push"
fi

echo "提交信息: $COMMIT_MSG"
echo "提交的文件:"
git show --name-only --pretty=format: HEAD
